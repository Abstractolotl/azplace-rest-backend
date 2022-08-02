package de.abstractolotl.azplace.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserCooldownException extends ResponseStatusException {

    public UserCooldownException(){
        super(HttpStatus.FORBIDDEN, "You are currently on cooldown");
    }

}
