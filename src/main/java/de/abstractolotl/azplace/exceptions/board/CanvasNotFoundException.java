package de.abstractolotl.azplace.exceptions.board;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CanvasNotFoundException extends ResponseStatusException {

    public CanvasNotFoundException(int canvasId) {
        super(HttpStatus.NOT_FOUND, "Could not find Canvas with ID: " + canvasId);
    }

}
