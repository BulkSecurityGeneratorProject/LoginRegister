package com.aew.ManagmentAccount.payload;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/**
 * Define the request payloads that the APIs will use for singup a user.
 */
@Getter
@Setter
public class UserUpdateForm {

    @NotBlank
    @Size(min = 3, max = 50)
    private String firstName;

    @NotBlank
    @Size(min = 3, max = 50)
    private String lastName;

    @NotBlank
    @Size(max = 60)
    @Email
    private String email;

}