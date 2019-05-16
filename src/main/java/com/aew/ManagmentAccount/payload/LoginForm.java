package com.aew.ManagmentAccount.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/**
 * Define the request payloads that the APIs will use for login.
 */
@Getter
@Setter
public class LoginForm {

    @NotBlank
    @Size(min = 3, max = 60)
    private String username;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    // private Boolean rememberMe;

}