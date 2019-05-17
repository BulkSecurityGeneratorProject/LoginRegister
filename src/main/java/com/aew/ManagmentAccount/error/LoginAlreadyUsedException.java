package com.aew.ManagmentAccount.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Informa que un username ya se encuentra en uso. Retorna un Conflict code.
 * 
 * @author Adrian E. Wilgenhoff
 */

@ResponseStatus(HttpStatus.CONFLICT)
public class LoginAlreadyUsedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public LoginAlreadyUsedException() {
        super("Username is already in use");
    }
}
