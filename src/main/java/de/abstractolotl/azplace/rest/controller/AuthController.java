package de.abstractolotl.azplace.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.abstractolotl.azplace.exceptions.auth.AuthenticationException;
import de.abstractolotl.azplace.exceptions.auth.CASValidationException;
import de.abstractolotl.azplace.model.user.UserSession;
import de.abstractolotl.azplace.model.utility.AuthUser;
import de.abstractolotl.azplace.rest.api.AuthAPI;
import de.abstractolotl.azplace.service.AuthenticationService;
import de.abstractolotl.azplace.service.ElasticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.repositories.UserRepo;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController @Slf4j
public class AuthController implements AuthAPI {

    private final static JsonMapper jsonMapper = new JsonMapper();

    @Value("${oauth.redirectUrl}")
    private String redirectUrl;
    @Value("${oauth.authorizationEndpoint}")
    private String authorizationEndpoint;
    @Value("${oauth.tokenEndpoint}")
    private String tokenEndpoint;
    @Value("${oauth.userInfoEndpoint}")
    private String userInfoEndpoint;

    @Value("${oauth.clientId}")
    private String clientId;
    @Value("${oauth.clientSecret}")
    private String clientSecret;

    @Value("${oauth.frontendUrl}")
    private String frontendUrl;

    private final AuthenticationService authService;
    private final ElasticService elasticService;

    private final UserRepo userRepo;

    private final UserSession session;

    @Autowired
    public AuthController(AuthenticationService authService, ElasticService elasticService, UserRepo userRepo, UserSession session) {
        this.authService = authService;
        this.elasticService = elasticService;
        this.userRepo = userRepo;
        this.session = session;
    }

    @Override
    public String login(String hostName) {
        String url = authorizationEndpoint + "?response_type=code&client_id=" + clientId + "&redirect_uri=" + redirectUrl + "&scope=openid profile";
        return "<meta http-equiv=\"refresh\" content=\"0; url=" + url + "\" />";
    }

    @Override
    public ResponseEntity<String> verify(String hostname, String code) {
        String validateCode = validateCode(code);

        try {
            JsonNode json = jsonMapper.readValue(validateCode, ObjectNode.class);
            String accessToken = json.get("access_token").textValue();

            String userInfo = getUserInfo(hostname, accessToken);
            AuthUser authUser = jsonMapper.readValue(userInfo, AuthUser.class);
            User user = createOrGetUser(authUser);

            session.setUser(user);
            elasticService.logLogin();
        } catch (JsonProcessingException exception) {
            log.error("Error while parsing JSON", exception);
        }

        HttpHeaders headers = new HttpHeaders();
        String body = "<meta http-equiv=\"refresh\" content=\"0; url=" + frontendUrl + "\" />";
        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> logout(HttpSession httpSession) {
        session.setUser(null);
        httpSession.invalidate();
        return ResponseEntity.ok("User logged out successfully");
    }

    private String validateCode(String code){
        final RestTemplate template = new RestTemplate();
        final String requestUrl = tokenEndpoint;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();

        map.add("grant_type", "authorization_code");

        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("code", code);
        map.add("redirect_uri", redirectUrl);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        try {
            return template.postForObject(requestUrl, request, String.class);
        } catch (HttpClientErrorException e) {
            throw new AuthenticationException(e.getResponseBodyAsString());
        }
    }

    private String getUserInfo(String hostname, String accessToken){
        final RestTemplate template = new RestTemplate();
        final String requestUrl = userInfoEndpoint;

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set(HttpHeaders.HOST, hostname);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        try {
            HttpEntity request = new HttpEntity(headers);
            ResponseEntity<String> response = template.exchange(requestUrl, HttpMethod.GET, request, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new AuthenticationException(e.getResponseBodyAsString());
        }
    }

    private User createOrGetUser(AuthUser cas) {
        var userResp = userRepo.findByInsideNetIdentifier(cas.getInsideNetIdentifier());

        if(userResp.isPresent()) return userResp.get();

        User user = cas.createUser();
        userRepo.save(user);
        return user;
    }

    private JsonNode parseCASResponse(String response) {
        JsonNode json;
        try {
            json = jsonMapper.readValue(response, ObjectNode.class).get("serviceResponse");
        } catch (Exception e) {
            throw new CASValidationException("Mapping JSON failed");
        }

        if(!json.has("authenticationSuccess"))
            throw new CASValidationException("Json information missing");

        return json;
    }

    private AuthUser getUserDataFromCASResponse(String response) {
        JsonNode parsedResponse = parseCASResponse(response);

        if(parsedResponse.has("authenticationFailure")){
            throw new AuthenticationException(parsedResponse.get("authenticationFailure").get("code").textValue());
        }

        JsonNode attributes = getValue(parsedResponse.get("authenticationSuccess"), "attributes", true);

        if(getAttribute(attributes, "firstName", true).startsWith("pseudo")
                || getAttribute(attributes, "firstName", true).startsWith("Pseudo")){
            throw new AuthenticationException("Pseudo accounts are not allowed");
        }

        if(getAttribute(attributes, "firstName", true).startsWith("tool")){
            throw new AuthenticationException("Tool accounts are not allowed");
        }

        Matcher matcher = Pattern.compile("^sea[a-z]{2}[0-9].*").matcher(getAttribute(attributes, "username", true));
        if(matcher.matches()){
            throw new AuthenticationException("Secondary accounts are not allowed");
        }

        return AuthUser.builder()
                .firstName(getAttribute(attributes, "firstName", true))
                .lastName(getAttribute(attributes, "lastName", false))
                .insideNetIdentifier(getAttribute(attributes, "personId", true))
                .build();
    }

    private JsonNode getValue(JsonNode node, String key, boolean required) {
        if (node.has(key)) {
            return node.get(key);
        }
        if (required) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required key \"" + key + "\" is missing");
        }
        return null;
    }

    private String getAttribute(JsonNode node, String key, boolean required){
        JsonNode value = getValue(node, key, required);

        if(value == null){
            return "";
        }

        return value.get(0).textValue();
    }

}