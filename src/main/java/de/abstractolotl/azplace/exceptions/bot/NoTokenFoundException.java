package de.abstractolotl.azplace.exceptions.bot;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoTokenFoundException extends ResponseStatusException {

    public NoTokenFoundException() {
        super(HttpStatus.NOT_FOUND, "No BotToken is related to your account");
    }

}
