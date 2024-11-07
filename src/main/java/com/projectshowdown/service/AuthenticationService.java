package com.projectshowdown.service;

import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.dto.UserMapper;
import com.projectshowdown.dto.VerifyUserDto;
import com.projectshowdown.util.DateTimeUtils;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class AuthenticationService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    public void verifyUser(VerifyUserDto input) throws ExecutionException, InterruptedException {
        Optional<UserDTO> optionalUser = Optional.ofNullable(userService.getUser(userService.getUserIdByEmail(input.getEmail())));
        if (optionalUser.isPresent()) {
            UserDTO user = optionalUser.get();
            if (DateTimeUtils.isExpired(user.getVerificationCodeExpiresAt())) {
                throw new RuntimeException("Verification code has expired");
            }
            if (user.getVerificationCode().equals(input.getVerificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpiresAt(null);
                userService.updateUser(userService.getUserIdByEmail(input.getEmail()), UserMapper.toMap(user));
            } else {
                throw new RuntimeException("Invalid verification code");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void resendVerificationCode(String email) throws ExecutionException, InterruptedException {
        Optional<UserDTO> optionalUser = Optional.ofNullable(userService.getUser(userService.getUserIdByEmail(email)));
        if (optionalUser.isPresent()) {
            UserDTO user = optionalUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
            user.setVerificationCode(userService.generateVerificationCode());
            user.setVerificationCodeExpiresAt(DateTimeUtils.toEpochSeconds(LocalDateTime.now().plusHours(1)));
            sendVerificationEmail(user);
            userService.updateUser(userService.getUserIdByEmail(email), UserMapper.toMap(user));
        } else {
            throw new RuntimeException("User not found");
        }
    }

    private void sendVerificationEmail(UserDTO user) { //TODO: Update with company logo
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            // Handle email sending exception
            throw new RuntimeException("Failed to send verification email");
        }
    }

}
