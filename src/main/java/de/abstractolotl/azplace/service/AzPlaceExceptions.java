package de.abstractolotl.azplace;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AzPlaceExceptions {

    public static class CanvasNotFoundExeption extends ResponseStatusException {
        public CanvasNotFoundExeption(int canvasId) {
            super(HttpStatus.NOT_FOUND, "Could not find Canvas with ID: " + canvasId);
        }
    }

    public static class SessionNotAuthorizedException extends ResponseStatusException {
        public SessionNotAuthorizedException() {
            super(HttpStatus.UNAUTHORIZED, "Session is not Authorized");
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

    public static class CASValidationException extends ResponseStatusException {

        public CASValidationException(String furtherInformation) {
            super(HttpStatus.INTERNAL_SERVER_ERROR, "Validate Ticket by CAS went wrong: " + furtherInformation);
        }
    }

    public static class AuthenticationException extends ResponseStatusException {

        public AuthenticationException(String furtherInformation) {
            super(HttpStatus.UNAUTHORIZED, "Authentication failed: " + furtherInformation);
        }

        public AuthenticationException() {
            super(HttpStatus.UNAUTHORIZED, "Authentication failed.");
        }
    }

}