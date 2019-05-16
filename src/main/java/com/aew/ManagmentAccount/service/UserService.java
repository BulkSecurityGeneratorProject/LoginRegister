package com.aew.ManagmentAccount.service;

import java.util.Optional;

import com.aew.ManagmentAccount.domain.User;
import com.aew.ManagmentAccount.payload.PasswordChangeForm;
import com.aew.ManagmentAccount.payload.SignUpForm;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    User registerUser(SignUpForm signUpForm);

    Optional<User> activateRegistration(String key);

    void changePassword(PasswordChangeForm passwordChangeForm);

    void updateUser(String firstName, String lastName, String email);

    Optional<User> findByEmailIgnoreCase(String mail);

    void deleteUser(String login);

    Page<User> getAllUsers(Pageable pageable);

}