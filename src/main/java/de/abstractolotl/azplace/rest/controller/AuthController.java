package de.abstractolotl.azplace.rest.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.abstractolotl.azplace.AzPlaceExceptions.*;
import de.abstractolotl.azplace.model.user.UserSession;
import de.abstractolotl.azplace.model.utility.CASUser;
import de.abstractolotl.azplace.rest.api.AuthAPI;
import de.abstractolotl.azplace.service.AuthenticationService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.repositories.UserRepo;

@RestController
public class AuthController implements AuthAPI {

    @Value("${app.cas.redirecturl}")
    private String redirectUrl;
    @Value("${app.defaultKeyValidTime}")
    private int    defaultKeyValidTime;

    @Autowired
    private AuthenticationService authService;

    @Autowired private UserRepo userRepo;

    @Autowired private UserSession session;

    @SneakyThrows
    @Override
    public ResponseEntity<String> verify(String ticket) {
        CASUser casUser = authService.validateTicket(ticket);
        initNewSession(casUser);

        HttpHeaders headers = new HttpHeaders();
        String body = "<meta http-equiv=\"refresh\" content=\"0; url=" + redirectUrl + "\" />";
        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

    @Override
    public void logout(String sessionKey) {
        //TODO
    }

    @Override
    public UserSession getSession() {
        return session;
    }

    @Override
    public boolean isSessionValid() {
        return authService.isSessionValid();
    }

    private void initNewSession(CASUser casUser) {
        User user = createOrGetUser(casUser);
        //session.setUser(user);
    }

    private User createOrGetUser(CASUser cas) {
        var userResp = userRepo.findByInsideNetIdentifier(cas.getInsideNetIdentifier());

        if(userResp.isPresent()) return userResp.get();

        User user = cas.createUser();
        userRepo.save(user);
        return user;
    }

}