package com.aew.ManagmentAccount.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Informa que el mail ya se encuentra en uso. Retorna un Conflict code.
 * 
 * @author Adrian E. Wilgenhoff
 */

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyUsedException extends RuntimeException {

    private static final long serialVersionUID = 2607956101009753524L;

    public EmailAlreadyUsedException() {
        super("Mail is already in use");
    }
}
