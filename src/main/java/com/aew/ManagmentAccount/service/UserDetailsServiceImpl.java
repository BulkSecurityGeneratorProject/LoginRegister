package com.aew.ManagmentAccount.service;

import com.aew.ManagmentAccount.domain.User;
import com.aew.ManagmentAccount.error.UserNotActivatedException;
import com.aew.ManagmentAccount.repository.UserRepository;
import com.aew.ManagmentAccount.security.UserPrinciple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authenticate a User or perform various role-based checks, Spring security
 * needs to load users details somehow.
 * 
 * @author Adrian
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService mailService;

    /**
     * Loads a user based on username or login id.
     * 
     * @param login
     * @return UserDetails object that Spring Security uses for performing various
     *         authentication and role based validations.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {

        return userRepository.findByLogin(login).map(user -> createSpringSecurityUser(login, user))
                .orElseThrow(() -> new UsernameNotFoundException("User " + login + " was not found in the database"));

    }

    private UserDetails createSpringSecurityUser(String login, User user) {
        if (!user.isActivated()) {
            mailService.sendActivationEmail(user);
            throw new UserNotActivatedException("User " + login + " was not activated, check your mail");

        }
        return UserPrinciple.build(user);
    }

}