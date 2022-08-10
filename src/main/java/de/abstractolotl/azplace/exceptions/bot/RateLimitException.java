package de.abstractolotl.azplace.exceptions.bot;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class RateLimitException extends ResponseStatusException {

    public RateLimitException() {
        super(HttpStatus.TOO_MANY_REQUESTS, "Rate Limit reached");
    }
}
