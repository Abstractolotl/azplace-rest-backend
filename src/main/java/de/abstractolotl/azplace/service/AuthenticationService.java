package de.abstractolotl.azplace.service;

import de.abstractolotl.azplace.AzPlaceExceptions.*;
import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.model.user.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@Service
public class AuthenticationService {


    @Autowired private UserSession userSession;

    public User authUser() {
        if (userSession.getUser() == null)
            throw new SessionNotAuthorizedException();

        return userSession.getUser();
    }


    public User authUserWithRole(String role) {
        User user = userSession.getUser();

        if (user == null)
            throw new SessionNotAuthorizedException();

        if (!hasRole(user, role))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); //TODO

        return userSession.getUser();
    }

    public boolean hasRole(User user, String role){
        return Arrays.stream(user.getRoleArray()).toList().contains(role);
    }

}
