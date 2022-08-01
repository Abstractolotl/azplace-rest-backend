package de.abstractolotl.azplace.controller;

import java.time.LocalDateTime;
import java.util.List;

import de.abstractolotl.azplace.api.AuthAPI;
import de.abstractolotl.azplace.model.user.Session;
import de.abstractolotl.azplace.model.user.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.repositories.SessionRepo;
import de.abstractolotl.azplace.repositories.UserRepo;

@RestController
@RequestMapping("/auth")
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

    @Override
    public String verify(String ticket) {
        final String       requestUrl = casUrl + "/serviceValidate?service=" + apiUrl + "&ticket=" + ticket;
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
    public void xmlShit(String xml) {
        createSessionKey(xml);
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
    public Session getSession(String sessionKey) {
        Session session = userSession.getSession();
        if (session == null) {
            final List<Session> sessionBySessionKey = sessionRepo.findSessionBySessionKey(sessionKey);
            if (sessionBySessionKey.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session key expected");
            }
            session = sessionBySessionKey.get(0);
        }
        return session;
    }

    @Override
    public boolean isSessionValid(String sessionKey) {
        Session session = getSession(sessionKey);
        final LocalDateTime now = LocalDateTime.now();
        if (session.getCreationDate().isAfter(now) || session.getExpireDate().isBefore(now)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session key expired");
        }
        return true;
    }

    private void createSessionKey(String casResponse) {
        final User    userDataFromCASResponse = getUserDataFromCASResponse(casResponse);
        final User    user                    = updateUserDataInDB(userDataFromCASResponse);
        final Session session                 = Session.createKey(defaultKeyValidTime, user);

        sessionRepo.save(session);
        userSession.setSession(session);
    }

    private User updateUserDataInDB(User userData) {
        final List<User> allByInsideNetIdentifier = userRepo.findAllByInsideNetIdentifier(userData.getInsideNetIdentifier());

        if (allByInsideNetIdentifier.isEmpty()) {
            userRepo.save(userData);
        }

        final User user = allByInsideNetIdentifier.get(0);

        user.setFirstName(userData.getFirstName());
        user.setLastName(userData.getLastName());

        return user;
    }

    private User getUserDataFromCASResponse(String response) {
        XmlMapper mapper = new XmlMapper();
        JsonNode  xmlParsed;
        try {
            xmlParsed = mapper.readValue(response, ObjectNode.class).get("authenticationsuccess");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Mapping XML failed");
        }
        if (xmlParsed == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AuthenticationFailure: " + xmlParsed.get("code"));
        } else {
            final JsonNode attributes = getValue(xmlParsed, "attributes", true);
            return User.builder()
                       .firstName(getValueString(attributes, "firstname", false))
                       .lastName(getValueString(attributes, "lastname", false))
                       .insideNetIdentifier(getValueString(attributes, "personid", true))
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

    private String getValueString(JsonNode node, String key, boolean required) {
        final JsonNode value = getValue(node, key, required);
        if (value == null) {
            return "";
        }
        return value.textValue();
    }
}