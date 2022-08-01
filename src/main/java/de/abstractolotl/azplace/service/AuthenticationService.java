package de.abstractolotl.azplace.service;

import de.abstractolotl.azplace.model.user.Session;
import de.abstractolotl.azplace.model.user.User;
import de.abstractolotl.azplace.model.user.UserSession;
import de.abstractolotl.azplace.repositories.SessionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthenticationService {

    @Autowired private UserSession userSession;
    @Autowired private SessionRepo sessionRepo;

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

    public boolean isSessionValid(String sessionKey) {
        Session session = getSession(sessionKey);
        final LocalDateTime now = LocalDateTime.now();
        if (session.getCreationDate().isAfter(now) || session.getExpireDate().isBefore(now)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session key expired");
        }
        return true;
    }

    public boolean hasRole(User user, String role){
        return user.getRole().equalsIgnoreCase(role);
    }

}
