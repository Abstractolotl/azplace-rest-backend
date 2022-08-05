package de.abstractolotl.azplace.exceptions.board;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PixelOwnerNotFoundException extends ResponseStatusException {

    public PixelOwnerNotFoundException(int x, int y, int canvasId) {
        super(HttpStatus.NOT_FOUND, "The pixel (" + x + ", " + y + ") has currently no owner on Canvas " + canvasId);
    }

}
