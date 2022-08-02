package de.abstractolotl.azplace.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class SessionMissingException extends ResponseStatusException {

    public SessionMissingException() {
        super(HttpStatus.UNAUTHORIZED, "Session data is missing");
    }

}
