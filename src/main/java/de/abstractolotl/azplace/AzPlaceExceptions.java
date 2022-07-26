package de.abstractolotl.azplace;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AzPlaceExceptions {

    public static class CanvasNotFoundExeption extends RuntimeException {
        public CanvasNotFoundExeption(int canvasId) {
            super("Could not find Canvas with ID: " + canvasId);
        }
    }

    public static class PixelOutOfBoundsException extends RuntimeException {
        public PixelOutOfBoundsException(int x, int y, int w, int h) {
            super("Pixel coordinates are out of canvas bounds:\nCanvas: (" + w + ", " + h + ")\nRequested Pixel: (" + x + ", " + y + ")");
        }
    }

    public static class IllegalPixelCoordsException extends RuntimeException {
        public IllegalPixelCoordsException() {
            super("Pixel coordinates cannot be negative!");
        }
    }

}
