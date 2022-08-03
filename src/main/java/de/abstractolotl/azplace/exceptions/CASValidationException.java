package de.abstractolotl.azplace.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;


@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CASValidationException extends ResponseStatusException {

    public CASValidationException(String furtherInformation) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Validate Ticket by CAS went wrong: " + furtherInformation);
    }
}
