package de.abstractolotl.azplace.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.abstractolotl.azplace.AzPlaceExceptions;
import de.abstractolotl.azplace.exceptions.SessionMissingException;
import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.model.user.UserSession;
import de.abstractolotl.azplace.model.utility.CASUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
public class AuthenticationService {

    private final static JsonMapper jsonMapper = new JsonMapper();

    @Autowired private UserSession userSession;
    @Value("${app.cas.url}") private String casUrl;
    @Value("${app.cas.apiurl}") private String apiUrl;

    public CASUser validateTicket(String ticket) {
        final RestTemplate template = new RestTemplate();

        final String requestUrl = casUrl + "/serviceValidate?service=" + apiUrl + "&ticket=" + ticket +"&format=json";
        try {
            String response = template.getForObject(requestUrl, String.class);
            return getUserDataFromCASResponse(response);
        } catch (HttpClientErrorException e) {
            throw new AzPlaceExceptions.CASValidationException(e.getResponseBodyAsString());
        }
    }

    private JsonNode parseCASResponse(String response) {
        JsonNode json;
        try {
            json = jsonMapper.readValue(response, ObjectNode.class).get("serviceResponse");
        } catch (Exception e) {
            throw new AzPlaceExceptions.CASValidationException("Mapping JSON failed");
        }

        if(!json.has("authenticationSuccess"))
            throw new AzPlaceExceptions.CASValidationException("Json information missing");

        return json;
    }

    private CASUser getUserDataFromCASResponse(String response) {
        JsonNode parsedResponse = parseCASResponse(response);

        if(parsedResponse.has("authenticationFailure")){
            throw new AzPlaceExceptions.AuthenticationException(parsedResponse.get("authenticationFailure").get("code").textValue());
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

    public boolean isSessionValid() {
        return true;
    }

    public boolean hasRole(String role){
        //return hasRole(session.getUser(), role);
        return hasRole(null, role);
    }

    public boolean hasRole(User user, String role){
        return Arrays.stream(user.getRoleArray()).toList().contains(role);
    }

    public User getUserFromSession(){
        //return userSession.getUser();
        return null;
    }

}
