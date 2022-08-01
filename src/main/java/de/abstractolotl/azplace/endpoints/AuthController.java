package de.abstractolotl.azplace.endpoints;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import de.abstractolotl.azplace.model.Session;
import de.abstractolotl.azplace.model.User;
import de.abstractolotl.azplace.model.UserSession;
import de.abstractolotl.azplace.repositories.SessionRepo;
import de.abstractolotl.azplace.repositories.UserRepo;


@Component
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
    private UserSession userSession;
    @Autowired
    private SessionRepo sessionRepo;
    @Autowired
    private UserRepo    userRepo;

    /**
     * Get ticket
     * <p>
     * Request an CAS mit ticket -> Token
     * Endpoint
     */

    @Override
    public String verify(@RequestParam("ticket") String ticket) {
        final String       requestUrl = casUrl + "/serviceValidate?service=" + apiUrl + "&ticket=" + ticket;
        final RestTemplate template   = new RestTemplate();
        String             response;
        try {
            response = template.getForObject(requestUrl, String.class);
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, e.getResponseBodyAsString()); //TODO: Better Error Handling
        }

        return response;

//        boolean validTicket = false; //TODO: check if ticket is valid and get response (there is the user name in it)
//        if (validTicket) {
//
//            //TODO: create SessionKey nad put to db
//            // make user Session valid???
//            createSessionKey();
//
//            return "<meta http-equiv=\"refresh\" content=\"0; url=" + redirectUrl + "\" />";
//        }
//        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ticket invalid!");
    }

    @Override
    public String logout(String sessionKey) {
//        userSession.setSession(null);
        //TODO: remove from DB
        return null;
    }

    private void createSessionKey() {
//        final Session session = Session.createKey(defaultKeyValidTime, getUser());
//        sessionRepo.save(session);
//        userSession.setSession(session);
    }

    @Override
    public String xmlShit(String xml) {
        return getUserAsString(xml);
    }

    private boolean isSessionValid() {
        final Session       session = userSession.getSession();
        final LocalDateTime now     = LocalDateTime.now();
        if (session.getCreationDate().isAfter(now) || session.getExpireDate().isBefore(now)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session key expired");
        }
        return true;
    }

    private String getUserAsString(String response) {
        XmlMapper mapper = new XmlMapper();
        JsonNode  authFailureNode;
        JsonNode  authSuccessNode;
        try {
            authFailureNode = mapper.readValue(response, ObjectNode.class).get("authenticationFailure");
            authSuccessNode = mapper.readValue(response, ObjectNode.class).get("authenticationSuccess");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (authFailureNode != null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AuthenticationFailure: " + authFailureNode.get("code"));
        }
        if (authSuccessNode != null) {
            if (authSuccessNode.has("user")) {
                return authSuccessNode.get("user").textValue();
            }
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Parsing XML Failed"); //TODO:
    }

    private User getUser(String userString) {
        final List<User> allByInsideNetIdentifier = userRepo.findAllByInsideNetIdentifier(userString);
        if (allByInsideNetIdentifier.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No user \"" + userString + "\" found ");
        }
        return allByInsideNetIdentifier.get(0);
    }
}