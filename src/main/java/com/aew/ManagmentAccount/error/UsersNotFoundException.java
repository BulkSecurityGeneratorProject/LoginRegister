package com.aew.ManagmentAccount.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Informa que un usuario no ha sido encontrado para ese valor de key. Retorna
 * un Conflict code.
 * 
 * @author Adrian E. Wilgenhoff
 */

@ResponseStatus(HttpStatus.CONFLICT)
public class UsersNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public UsersNotFoundException(String msg) {
        super(msg);
    }
}
