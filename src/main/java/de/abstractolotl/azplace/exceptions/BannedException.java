package de.abstractolotl.azplace.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class BannedException extends ResponseStatusException {

    public BannedException(String message, Throwable cause) {
        super(HttpStatus.FORBIDDEN, message, cause);
    }

    public BannedException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
