package de.abstractolotl.azplace;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AzPlaceExceptions {

    public static class CanvasNotFoundExeption extends ResponseStatusException {
        public CanvasNotFoundExeption(int canvasId) {
            super(HttpStatus.NOT_FOUND, "Could not find Canvas with ID: " + canvasId);
        }
    }

    public static class NoUserInSession extends ResponseStatusException {
        public NoUserInSession() {
            super(HttpStatus.NOT_FOUND, "Could not get User from Session");
        }
    }

    public static class PixelOutOfBoundsException extends ResponseStatusException {
        public PixelOutOfBoundsException(int x, int y, int w, int h) {
            super(HttpStatus.BAD_REQUEST, "Pixel coordinates are out of canvas bounds:\nCanvas: (" + w + ", " + h + ")\nRequested Pixel: (" + x + ", " + y + ")");
        }
    }

    public static class IllegalPixelCoordsException extends ResponseStatusException {
        public IllegalPixelCoordsException() {
            super(HttpStatus.BAD_REQUEST, "Pixel coordinates cannot be negative!");
        }
    }

}
