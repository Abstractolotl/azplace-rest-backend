package de.abstractolotl.azplace.rest.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.abstractolotl.azplace.exceptions.AuthenticationException;
import de.abstractolotl.azplace.exceptions.CASValidationException;
import de.abstractolotl.azplace.model.user.UserSession;
import de.abstractolotl.azplace.model.utility.CASUser;
import de.abstractolotl.azplace.rest.api.AuthAPI;
import de.abstractolotl.azplace.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.repositories.UserRepo;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AuthController implements AuthAPI {

    private final static JsonMapper jsonMapper = new JsonMapper();

    @Value("${app.cas.redirecturl}") private String redirectUrl;
    @Value("${app.defaultKeyValidTime}") private int defaultKeyValidTime;
    @Value("${app.cas.url}") private String casUrl;
    @Value("${app.cas.apiurl}") private String apiUrl;

    @Autowired private AuthenticationService authService;

    @Autowired private UserRepo userRepo;

    @Autowired private UserSession session;

    @Override
    public ResponseEntity<String> verify(String ticket) {
        CASUser casUser = validateTicket(ticket);
        User user = createOrGetUser(casUser);

        session.setUser(user);

        HttpHeaders headers = new HttpHeaders();
        String body = "<meta http-equiv=\"refresh\" content=\"0; url=" + redirectUrl + "\" />";
        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

    @Override
    public void logout(String sessionKey) {
        session.setUser(null);
        //TODO: invalidate session
    }

    public CASUser validateTicket(String ticket) {
        final RestTemplate template = new RestTemplate();

        final String requestUrl = casUrl + "/serviceValidate?service=" + apiUrl + "&ticket=" + ticket +"&format=json";
        try {
            String response = template.getForObject(requestUrl, String.class);
            return getUserDataFromCASResponse(response);
        } catch (HttpClientErrorException e) {
            throw new CASValidationException(e.getResponseBodyAsString());
        }
    }

    private User createOrGetUser(CASUser cas) {
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

    private CASUser getUserDataFromCASResponse(String response) {
        JsonNode parsedResponse = parseCASResponse(response);

        if(parsedResponse.has("authenticationFailure")){
            throw new AuthenticationException(parsedResponse.get("authenticationFailure").get("code").textValue());
        }

        JsonNode attributes = getValue(parsedResponse.get("authenticationSuccess"), "attributes", true);
        return CASUser.builder()
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