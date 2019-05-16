package com.aew.ManagmentAccount.error;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Informa que hay un error en la contraseña. Retorna un Conflict code.
 * 
 * @author Adrian E. Wilgenhoff
 */

@ResponseStatus(HttpStatus.CONFLICT)
public class InvalidPasswordException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    public InvalidPasswordException() {
        super("Incorrect password");
    }
}
