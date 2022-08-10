package de.abstractolotl.azplace.exceptions.board;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class OutsideTimespanException extends ResponseStatusException {

    public OutsideTimespanException() {
        super(HttpStatus.FORBIDDEN, "The canvas has not started or already ended");
    }

}
