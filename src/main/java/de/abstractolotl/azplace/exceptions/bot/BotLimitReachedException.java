package de.abstractolotl.azplace.exceptions.bot;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class BotLimitReachedException extends ResponseStatusException {

    public BotLimitReachedException() {
        super(HttpStatus.FORBIDDEN, "You have reached the maximum amount of bots");
    }

}
