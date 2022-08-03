package de.abstractolotl.azplace.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PixelOutOfBoundsException extends ResponseStatusException {

    public PixelOutOfBoundsException(int x, int y, int w, int h) {
        super(HttpStatus.BAD_REQUEST, "Pixel coordinates are out of canvas bounds:\nCanvas: (" + w + ", " + h + ")\nRequested Pixel: (" + x + ", " + y + ")");
    }

}
