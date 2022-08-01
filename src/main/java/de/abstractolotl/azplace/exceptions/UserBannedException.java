package de.abstractolotl.azplace.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserBannedException extends ResponseStatusException {

    public UserBannedException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }

    public UserBannedException(){
        super(HttpStatus.FORBIDDEN, "You are banned from this board");
    }
}
