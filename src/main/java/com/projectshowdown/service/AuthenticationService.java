package com.projectshowdown.service;

import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.dto.VerifyUserDto;
import com.projectshowdown.util.DateTimeUtils;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
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
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
            if (DateTimeUtils.isExpired(user.getVerificationCodeExpiresAt())) {
                throw new RuntimeException("Verification code has expired");
            }
            if (user.getVerificationCode().equals(input.getVerificationCode())) {

                HashMap<String, Object> verify = new HashMap<>();
                verify.put("enabled", true);
                verify.put("verificationCode", null);
                verify.put("verificationCodeExpiresAt", null);
                userService.updateUser(userService.getUserIdByEmail(input.getEmail()), verify);

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

            // generate new verification code and expiration
            String verificationCode = userService.generateVerificationCode();
            long verificationCodeExpiresAt = DateTimeUtils.toEpochSeconds(LocalDateTime.now().plusHours(1));

            //set new verification code and expiration to userDTO
            user.setVerificationCode(verificationCode);
            user.setVerificationCodeExpiresAt(verificationCodeExpiresAt);

            sendVerificationEmail(user);

            // update user verification code and expiration
            HashMap<String, Object> verify = new HashMap<>();
            verify.put("verificationCode", verificationCode);
            verify.put("verificationCodeExpiresAt", verificationCodeExpiresAt);
            userService.updateUser(userService.getUserIdByEmail(email), verify);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void sendVerificationEmail(UserDTO user) { //TODO: Update with company logo
        String subject = "Account Verification";
        String verificationCode = user.getVerificationCode();
        String verificationLink = "http://localhost:3000/verify?email=" + user.getEmail() + "&code=" + verificationCode;

        String htmlMessage =
                "<!DOCTYPE html>" +
                        "<html lang=\"en\">" +
                        "<head><meta charset=\"UTF-8\"></head>" +
                        "<body style=\"margin:0;padding:0;font-family:Arial,sans-serif;background-color:#f3eeea;\">" +
                        "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width:600px;margin:0 auto;\">" +
                        "<tr><td style=\"padding:20px;\">" +
                        "<div style=\"background-color:white;border-radius:10px;box-shadow:0 4px 6px rgba(0,0,0,0.1);padding:30px;text-align:center;\">" +
                        "<img src=\"cid:showdown-logo.png\" alt=\"Logo\" style=\"width:150px;margin-bottom:1.5rem;\"/>" +
                        "<h2 style=\"color:#776b5d;margin-bottom:1rem;font-size:24px;\">Verify Your Account</h2>" +
                        "<p style=\"color:#666;margin-bottom:1.5rem;font-size:16px;\">Please use the verification code below or click the button to verify your account:</p>" +
                        "<div style=\"background-color:#f3eeea;padding:20px;border-radius:5px;margin:25px 0;\">" +
                        "<p style=\"font-size:32px;letter-spacing:0.2rem;color:#776b5d;font-weight:bold;margin:0;\">" + verificationCode + "</p>" +
                        "</div>" +
                        "<a href=\"" + verificationLink + "\" style=\"display:inline-block;background-color:#776b5d;color:white;text-decoration:none;padding:12px 30px;border-radius:5px;font-size:16px;margin:20px 0;\">Verify Account</a>" +
                        "<p style=\"color:#666;font-size:14px;margin-bottom:25px;\">This code will expire in 1 hour.</p>" +
                        "<div style=\"margin-top:30px;border-top:1px solid #b0a695;padding-top:20px;\">" +
                        "<p style=\"color:#666;font-size:12px;margin:0;\">If you didn't request this verification, please ignore this email.</p>" +
                        "<p style=\"color:#666;font-size:12px;margin-top:10px;\">Or copy and paste this link into your browser:<br>" +
                        "<span style=\"color:#776b5d;word-break:break-all;\">" + verificationLink + "</span></p>" +
                        "</div>" +
                        "</div>" +
                        "</td></tr>" +
                        "</table>" +
                        "</body>" +
                        "</html>";

        try {
            emailService.sendEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            // Handle email sending exception
            throw new RuntimeException("Failed to send verification email");
        }
    }

}
