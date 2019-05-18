package com.aew.ManagmentAccount.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.aew.ManagmentAccount.domain.User;
import com.aew.ManagmentAccount.error.EmailAlreadyUsedException;
import com.aew.ManagmentAccount.error.EmailNotFoundException;
import com.aew.ManagmentAccount.error.InvalidPasswordException;
import com.aew.ManagmentAccount.error.LoginAlreadyUsedException;
import com.aew.ManagmentAccount.error.UsersNotFoundException;
import com.aew.ManagmentAccount.payload.LoginForm;
import com.aew.ManagmentAccount.payload.PasswordChangeForm;
import com.aew.ManagmentAccount.payload.SignUpForm;
import com.aew.ManagmentAccount.payload.UserUpdateForm;
import com.aew.ManagmentAccount.repository.UserRepository;
import com.aew.ManagmentAccount.security.SecurityUtils;
import com.aew.ManagmentAccount.security.UserPrinciple;
import com.aew.ManagmentAccount.security.jwt.JwtProvider;
import com.aew.ManagmentAccount.security.jwt.JwtResponse;
import com.aew.ManagmentAccount.service.MailService;
import com.aew.ManagmentAccount.service.UserService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api/v1")
public class AccountController {

    private final Logger log = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtProvider jwtProvider;

    /**
     * POST /sigin : signin the user.
     * 
     * @param loginForm
     * @return JwtResponse Token para poder usar los demas endpoints del controlador
     * @throws UsersNotFoundException
     */
    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authorize(@Valid @RequestBody LoginForm loginForm)
            throws UsersNotFoundException {

        Optional<User> user = userRepository.findByLogin(loginForm.getUsername());
        if (!user.isPresent()) {
            throw new UsersNotFoundException("User could not be found");
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginForm.getUsername(), loginForm.getPassword());

        Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateJwtToken(authentication);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Autorizacion", "Bearer " + jwt);
        return ResponseEntity.ok(new JwtResponse(jwt));
        // return new ResponseEntity<>(new JwtResponse(jwt), httpHeaders,
        // HttpStatus.OK);
    }

    /**
     * POST /register : register the user.
     *
     * @param signUpForm the managed user View Model
     * @throws InvalidPasswordException  400 (Bad Request) if the password is
     *                                   incorrect
     * @throws EmailAlreadyUsedException 400 (Bad Request) if the email is already
     *                                   used
     * @throws LoginAlreadyUsedException 400 (Bad Request) if the login is already
     *                                   used
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody SignUpForm signUpForm) throws InvalidPasswordException {
        if (!checkPasswordLength(signUpForm.getPassword())) {
            throw new InvalidPasswordException();
        }
        User user = userService.registerUser(signUpForm);
        mailService.sendActivationEmail(user);
    }

    /**
     * GET /activate : activate the registered user. Solo seguir el link del mail.
     * 
     * @param key the activation key
     * @throws UsersNotFoundExceptionForThisKey
     */
    @GetMapping("/user/activation/{key}")
    public String activateAccount(@PathVariable("key") String key) throws UsersNotFoundException {
        Optional<User> user = userService.activateRegistration(key);
        if (!user.isPresent()) {
            throw new UsersNotFoundException("No user was found for this activation key");
        }
        return "Usuario Activado";
        // Redirijir para hacer un autologueo?.
    }

    /**
     * GET /authenticate : check if the user is authenticated, and return its login
     * name. Necesita un Token.
     *
     * @param request the HTTP request
     * @return the login if the user is authenticated
     */
    @GetMapping("/authenticate")
    // @PostAuthorize("authenticated")
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * POST /account : update the current user information. Modify firstname,
     * lastname and email.
     *
     * @param userDTO the current user information
     * @throws EmailAlreadyUsedException 400 (Bad Request) if the email is already
     *                                   used
     * @throws UsersNotFoundException
     */
    @PostMapping("/account/update")
    public void saveAccount(@Valid @RequestBody UserUpdateForm userUpdateForm)
            throws UsersNotFoundException, EmailAlreadyUsedException {
        String userLogin = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new UsersNotFoundException("Current user login not found"));
        Optional<User> existingUser = userRepository.findByEmailIgnoreCase(userUpdateForm.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userLogin))) {
            throw new EmailAlreadyUsedException();
        }
        Optional<User> user = userRepository.findByLogin(userLogin);
        if (!user.isPresent()) {
            throw new UsersNotFoundException("User could not be found");
        }
        userService.updateUser(userUpdateForm.getFirstName(), userUpdateForm.getLastName(), userUpdateForm.getEmail());
    }

    @GetMapping("/me")
    public UserPrinciple getCurrentUser(@AuthenticationPrincipal UserPrinciple currentUser) {
        return currentUser;
    }

    @GetMapping("/me2")
    public ResponseEntity<?> currentUser(@AuthenticationPrincipal UserDetails userDetails) {
        Map<Object, Object> model = new HashMap<>();
        model.put("username", userDetails.getUsername());
        model.put("active", userDetails.isEnabled());
        model.put("roles", userDetails.getAuthorities().stream().map(a -> ((GrantedAuthority) a).getAuthority())
                .collect(Collectors.toList()));
        return ResponseEntity.ok(model);
    }

    /**
     * POST /account/change-password : changes the current user's password. Necesita
     * un token
     * 
     * @param passwordChangeDto current and new password
     * @throws InvalidPasswordException 400 (Bad Request) if the new password is
     *                                  incorrect
     */
    @PostMapping(path = "/account/change-password")
    public void changePassword(@RequestBody PasswordChangeForm passwordChangeForm) throws InvalidPasswordException {
        if (!checkPasswordLength(passwordChangeForm.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        userService.changePassword(passwordChangeForm);
    }

    /**
     * POST /account/send-reset-password/: Send an email with the password of the
     * user
     *
     * @param mail the mail of the user
     * @throws EmailNotFoundException 400 (Bad Request) if the email address is not
     *                                registered
     */
    @PostMapping(path = "/account/send-reset-password")
    public void requestPasswordReset(@RequestBody String mail) throws EmailNotFoundException {
        mailService.sendPasswordMail(userService.findByEmailIgnoreCase(mail).orElseThrow(EmailNotFoundException::new));
    }

    private static boolean checkPasswordLength(String password) {
        return !StringUtils.isEmpty(password) && password.length() >= 4 && password.length() <= 100;
    }

}
