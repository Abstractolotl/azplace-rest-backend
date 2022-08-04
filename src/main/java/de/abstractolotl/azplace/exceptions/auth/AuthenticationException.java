package de.abstractolotl.azplace.exceptions.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticationException extends ResponseStatusException {

    public AuthenticationException(String furtherInformation) {
        super(HttpStatus.UNAUTHORIZED, "Authentication failed: " + furtherInformation);
    }

    public AuthenticationException() {
        super(HttpStatus.UNAUTHORIZED, "Authentication failed.");
    }

}
