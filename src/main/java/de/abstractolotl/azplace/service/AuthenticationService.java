package de.abstractolotl.azplace.service;

import de.abstractolotl.azplace.exceptions.SessionMissingException;
import de.abstractolotl.azplace.model.user.Session;
import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.model.user.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
public class AuthenticationService {

    @Autowired
    private UserSession userSession;

    public Session getSession() {
        if(userSession.getSession() == null)
            throw new SessionMissingException();

        return userSession.getSession();
    }

    public boolean isSessionValid() {
        Session session = getSession();
        final LocalDateTime now = LocalDateTime.now();
        if (session.getCreationDate().isAfter(now) || session.getExpireDate().isBefore(now)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session key expired");
        }
        return true;
    }

    public User getUserFromSession() {
        return getSession().getUser();
    }

    public boolean hasRole(String role){
        return hasRole(getUserFromSession(), role);
    }

    public boolean hasRole(User user, String role){
        return Arrays.stream(user.getRoles()).toList().contains(role);
    }

}
