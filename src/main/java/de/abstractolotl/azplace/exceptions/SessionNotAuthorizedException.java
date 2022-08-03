package de.abstractolotl.azplace.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class SessionNotAuthorizedException extends ResponseStatusException {

    public SessionNotAuthorizedException() {
        super(HttpStatus.UNAUTHORIZED, "Session is not Authorized");
    }

}
