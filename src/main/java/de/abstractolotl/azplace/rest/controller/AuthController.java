package de.abstractolotl.azplace.rest.controller;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.abstractolotl.azplace.rest.api.AuthAPI;
import de.abstractolotl.azplace.model.user.Session;
import de.abstractolotl.azplace.model.user.UserSession;
import de.abstractolotl.azplace.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.repositories.SessionRepo;
import de.abstractolotl.azplace.repositories.UserRepo;

@RestController
public class AuthController implements AuthAPI {

    @Value("${app.cas.url}")
    private String casUrl;
    @Value("${app.cas.apiurl}")
    private String apiUrl;
    @Value("${app.cas.redirecturl}")
    private String redirectUrl;
    @Value("${app.defaultKeyValidTime}")
    private int    defaultKeyValidTime;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserSession userSession;
    @Autowired
    private SessionRepo sessionRepo;
    @Autowired
    private UserRepo    userRepo;

    @Override
    public String verify(String ticket) {
        final String       requestUrl = casUrl + "/serviceValidate?service=" + apiUrl + "&ticket=" + ticket +"&format=json";
        final RestTemplate template   = new RestTemplate();

        String             response;
        try {
            response = template.getForObject(requestUrl, String.class);
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Validate Ticket by CAS went wrong: " + e.getResponseBodyAsString());
        }

        createSessionKey(response);
        return "<meta http-equiv=\"refresh\" content=\"0; url=" + redirectUrl + "\" />";
    }

    @Override
    public void logout(String sessionKey) {
        userSession.setSession(null);
        final List<Session> sessionBySessionKeys = sessionRepo.findSessionBySessionKey(sessionKey);
        if (sessionBySessionKeys.isEmpty()) {
            return;
        }
        sessionRepo.delete(sessionBySessionKeys.get(0));
    }

    @Override
    public Session getSession() {
        return authenticationService.getSession();
    }

    @Override
    public boolean isSessionValid() {
        return authenticationService.isSessionValid();
    }

    private void createSessionKey(String casResponse) {
        final User    userDataFromCASResponse = getUserDataFromCASResponse(casResponse);
        final User    user                    = updateUserDataInDB(userDataFromCASResponse);
        final Session session                 = Session.createKey(defaultKeyValidTime, user);

        sessionRepo.save(session);
        userSession.setSession(session);
    }


    private User updateUserDataInDB(User userData) {
        if(!userRepo.existsByInsideNetIdentifier(userData.getInsideNetIdentifier())){
            return userRepo.save(userData);
        }

        User user = userRepo.findByInsideNetIdentifier(userData.getInsideNetIdentifier());

        user.setFirstName(userData.getFirstName());
        user.setLastName(userData.getLastName());

        return user;
    }

    private User getUserDataFromCASResponse(String response) {
        JsonMapper jsonMapper = new JsonMapper();

        JsonNode parsedResponse;
        try {
            parsedResponse = jsonMapper.readValue(response, ObjectNode.class).get("serviceResponse");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Mapping JSON failed");
        }

        if (parsedResponse == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed");
        } else {
            if(parsedResponse.has("authenticationFailure")){
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed: " +
                        parsedResponse.get("authenticationFailure").get("code").textValue());
            }

            if(!parsedResponse.has("authenticationSuccess"))
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Json information missing");

            JsonNode attributes = getValue(parsedResponse.get("authenticationSuccess"), "attributes", true);
            return User.builder()
                    .firstName(getAttribute(attributes, "firstName", true))
                    .lastName(getAttribute(attributes, "lastName", false))
                    .insideNetIdentifier(getAttribute(attributes, "personId", true))
                    .build();
        }
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