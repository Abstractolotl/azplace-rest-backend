package de.abstractolotl.azplace.exceptions.bot;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidTokenException extends ResponseStatusException {

    public InvalidTokenException() {
        super(HttpStatus.UNAUTHORIZED, "Bot token is invalid");
    }

}
