package de.abstractolotl.azplace.exceptions.board;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalPixelCoordsException extends ResponseStatusException {

    public IllegalPixelCoordsException() {
        super(HttpStatus.BAD_REQUEST, "Pixel coordinates cannot be negative!");
    }

}
