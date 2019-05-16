package com.aew.ManagmentAccount.service;

import java.nio.charset.StandardCharsets;

import javax.mail.internet.MimeMessage;

import com.aew.ManagmentAccount.domain.User;
import com.aew.ManagmentAccount.repository.UserRepository;
import com.aew.ManagmentAccount.util.RandomUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    private static final String BASE_URL = "http://localhost:8080/api/v1/user/activation/";

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String to, String subject, String content) {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom("noreply@NameOfApp");
            message.setSubject(subject);
            message.setText(content, true);
            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.warn("Email could not be sent to user '{}'", to, e);
            } else {
                log.warn("Email could not be sent to user '{}': {}", to, e.getMessage());
            }
        }
    }

    public void sendActivationEmail(User user) {
        log.debug("Sending activation email to '{}'", user.getEmail());
        sendEmail(user.getEmail(), "mail/activationEmail", BASE_URL + user.getActivationKey());
    }

    public void sendPasswordMail(User user) {
        log.debug("Sending password reset email to '{}'", user.getEmail());
        String passwordRandom = RandomUtil.generatePassword();
        user.setPassword(passwordEncoder.encode(passwordRandom));
        userRepository.save(user);
        sendEmail(user.getEmail(), "mail/passwordResetEmail", "Su password es: " + passwordRandom);
    }
}