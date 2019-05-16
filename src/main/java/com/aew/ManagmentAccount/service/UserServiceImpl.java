package com.aew.ManagmentAccount.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.aew.ManagmentAccount.domain.Authority;
import com.aew.ManagmentAccount.domain.RoleName;
import com.aew.ManagmentAccount.domain.User;
import com.aew.ManagmentAccount.error.InvalidPasswordException;
import com.aew.ManagmentAccount.payload.PasswordChangeForm;
import com.aew.ManagmentAccount.payload.SignUpForm;
import com.aew.ManagmentAccount.repository.AuthorityRepository;
import com.aew.ManagmentAccount.repository.UserRepository;
import com.aew.ManagmentAccount.security.SecurityUtils;
import com.aew.ManagmentAccount.util.RandomUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Register a new user.
     * 
     * @param signUpForm
     * @return user new user registered
     */
    public User registerUser(SignUpForm signUpForm) {

        userRepository.findByLogin(signUpForm.getUsername().toLowerCase()).ifPresent(existingUser -> {
            boolean removed = removeNonActivatedUser(existingUser);
            if (!removed) {
                // throw new LoginAlreadyUsedException();
            }
        });
        userRepository.findByEmailIgnoreCase(signUpForm.getEmail()).ifPresent(existingUser -> {
            boolean removed = removeNonActivatedUser(existingUser);
            if (!removed) {
                // throw new EmailAlreadyUsedException();
            }
        });
        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(signUpForm.getPassword());
        newUser.setLogin(signUpForm.getUsername().toLowerCase());
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(signUpForm.getFirstName());
        newUser.setLastName(signUpForm.getLastName());
        newUser.setEmail(signUpForm.getEmail().toLowerCase());
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findByName(RoleName.ROLE_USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    /**
     * Remove a user non activated.
     * 
     * @param existingUser current user waiting for register
     */
    private boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.isActivated()) {
            return false;
        }
        userRepository.delete(existingUser);
        return true;
    }

    /**
     * activate account user for the registration key.
     * 
     * @param key key
     * @return User activated user
     */
    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository.findOneByActivationKey(key).map(user -> {
            user.setActivated(true);
            user.setActivationKey(null);
            log.debug("Activated user: {}", user);
            return user;
        });
    }

    /**
     * Update basic information (first name, last name, email, language) for the
     * current user.
     *
     * @param firstName first name of user
     * @param lastName  last name of user
     * @param email     email id of user
     * @param langKey   language key
     * @param imageUrl  image URL of user
     */
    public void updateUser(String firstName, String lastName, String email) {
        SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findByLogin).ifPresent(user -> {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email.toLowerCase());
            log.debug("Changed Information for User: {}", user);
        });
    }

    /**
     * Delete a specific User from database.
     * 
     * @param login login of the user to delete.
     */
    public void deleteUser(String login) {
        userRepository.findByLogin(login).ifPresent(user -> {
            userRepository.delete(user);
            log.debug("Deleted User: {}", user);
        });
    }

    /***
     * Change the old password by a new password
     * 
     * @param passwordChangeForm old and new password
     * @param login              login the current user
     */
    public void changePassword(PasswordChangeForm passwordChangeForm) {

        SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findByLogin).ifPresent(user -> {
            String currentEncryptedPassword = user.getPassword();
            if (!passwordEncoder.matches(passwordChangeForm.getCurrentPassword(), currentEncryptedPassword)) {
                throw new InvalidPasswordException();
            }
            String encryptedPassword = passwordEncoder.encode(passwordChangeForm.getNewPassword());
            user.setPassword(encryptedPassword);
            log.debug("Changed password for User: {}", user);
        });
    }

    /**
     * Not activated users should be automatically deleted after 3 days. This is
     * scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(Instant.now().minus(1, ChronoUnit.DAYS))
                .forEach(user -> {
                    log.debug("Deleting not activated user {}", user.getLogin());
                    userRepository.delete(user);
                });
    }

    @Override
    public Optional<User> findByEmailIgnoreCase(String mail) {
        return userRepository.findByEmailIgnoreCase(mail);
    }

    /**
     * Get a list all user with pagination.
     * 
     * @param pageable
     * @return Page<User> return a page with list of user.
     */
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAllUsers(pageable);
    }
}