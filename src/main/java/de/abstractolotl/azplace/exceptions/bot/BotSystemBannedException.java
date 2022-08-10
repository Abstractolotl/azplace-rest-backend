package de.abstractolotl.azplace.exceptions.bot;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class BotSystemBannedException extends ResponseStatusException {

    public BotSystemBannedException(){
        super(HttpStatus.FORBIDDEN, "You are not allowed to use the bot system");
    }

}
