package com.aew.ManagmentAccount.error;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Informa que el usuario no ha activado su cuenta. Retorna un Conflict code.
 * 
 * @author Adrian E. Wilgenhoff
 */

@ResponseStatus(HttpStatus.CONFLICT)
public class UserNotActivatedException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    public UserNotActivatedException(String msg) {
        super(msg);
    }
}
