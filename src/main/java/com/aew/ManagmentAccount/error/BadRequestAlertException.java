package com.aew.ManagmentAccount.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * .
 * 
 * @author Adrian E. Wilgenhoff
 */

@ResponseStatus(HttpStatus.CONFLICT)
public class BadRequestAlertException extends Exception {

    private static final long serialVersionUID = 2607956101009753524L;

    public BadRequestAlertException(String msj) {
        super(msj);
    }
}
