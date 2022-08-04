package de.abstractolotl.azplace.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class MissingRoleException extends ResponseStatusException {

    public MissingRoleException() {
        super(HttpStatus.FORBIDDEN, "You are not allowed to perform this action");
    }

}
