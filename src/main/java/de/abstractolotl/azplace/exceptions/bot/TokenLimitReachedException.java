package de.abstractolotl.azplace.exceptions.bot;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class TokenLimitReachedException extends ResponseStatusException {

    public TokenLimitReachedException() {
        super(HttpStatus.FORBIDDEN, "There is already a token connected with your user");
    }

}
