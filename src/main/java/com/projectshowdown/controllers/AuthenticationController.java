package com.projectshowdown.controllers;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.dto.Verify2faDTO;
import com.projectshowdown.dto.VerifyUserDto;
import com.projectshowdown.service.AuthenticationService;
import com.projectshowdown.service.TwoFactorAuthService;
import com.projectshowdown.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import com.projectshowdown.configs.JwtUtil;
import com.projectshowdown.entities.User;

/**
 * Controller for handling authentication-related operations.
 * This includes login, account verification, two-factor authentication (2FA), 
 * and sending/resending verification codes or emails.
 */
@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private TwoFactorAuthService twoFactorAuthService;

    /**
     * Endpoint for logging in a user.
     *
     * @param userCredentials The {@link User} object containing the user's email and password.
     * @return A {@link ResponseEntity} containing a JWT token, user ID, and a flag indicating if 2FA is required.
     * @throws UsernameNotFoundException If the user does not exist.
     * @throws BadCredentialsException If the provided credentials are incorrect.
     */
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody User userCredentials) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userCredentials.getEmail());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userCredentials.getEmail(),
                            userCredentials.getPassword()));

            UserDTO user = userService.getUser(userService.getUserIdByEmail(userCredentials.getEmail()));

            if (!user.isEnabled()) {
                return ResponseEntity.badRequest().body("Account not verified. Please verify your account.");
            }

            if (user.getTwoFactorSecret() != null) {
                // 2FA is enabled, return a flag indicating 2FA is required
                return ResponseEntity.ok(Map.of("requiresTwoFactor", true, "userId", user.getId()));
            } else {
                // 2FA is not enabled, generate the JWT token
                String token = jwtUtil.generateToken(userDetails);
                
                return ResponseEntity.ok(Map.of("token", token, "requiresTwoFactor", false, "userId",user.getId()));
            }
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body("Account with the specified email does not exist");
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Incorrect password");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Authentication failed");
        }
    }

    /**
     * Enables two-factor authentication (2FA) for a user by generating a QR code.
     *
     * @param email The email address of the user.
     * @return A {@link ResponseEntity} containing a Base64-encoded QR code image.
     */
    @PostMapping("/enable-2fa")
    public ResponseEntity<?> enableTwoFactorAuth(@RequestParam String email) {
        try {
            String qrCodeImage = userService.enableTwoFactorAuth(userService.getUserIdByEmail(email));
            return ResponseEntity.ok().body(Map.of("qrCodeImage", qrCodeImage));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error enabling 2FA: " + e.getMessage());
        }
    }

    /**
     * Verifies a 2FA code entered by the user.
     *
     * @param verify2faDto A {@link Verify2faDTO} containing the user's email and 2FA code.
     * @return A {@link ResponseEntity} containing a JWT token if the code is valid.
     */
    @PostMapping("/verify-2fa")
    public ResponseEntity<?> verifyTwoFactorAuth(@RequestBody Verify2faDTO verify2faDto) {
        try {
            UserDTO user = userService.getUser(userService.getUserIdByEmail(verify2faDto.getEmail()));
            boolean isValid = twoFactorAuthService.verifyCode(user.getTwoFactorSecret(), verify2faDto.getCode());
            if (isValid) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(verify2faDto.getEmail());
                String token = jwtUtil.generateToken(userDetails);
                return ResponseEntity.ok(Map.of("token", token, "userId", user.getId()));
            } else {
                return ResponseEntity.badRequest().body("Invalid 2FA code");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error verifying 2FA code");
        }
    }

    /**
     * Verifies a user's account using a verification code.
     *
     * @param verifyUserDto A {@link VerifyUserDto} containing the user's email and verification code.
     * @return A {@link ResponseEntity} indicating the result of the verification process.
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        try {
            authenticationService.verifyUser(verifyUserDto);

            // Get user details after verification
            UserDTO user = userService.getUser(userService.getUserIdByEmail(verifyUserDto.getEmail()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(verifyUserDto.getEmail());

            if (user.getTwoFactorSecret() != null) {
                return ResponseEntity.ok(Map.of(
                        "status", "requires_2fa",
                        "message", "Account verified successfully. 2FA required."));
            } else {
                String token = jwtUtil.generateToken(userDetails);
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "token", token,
                            "userId", user.getId(),
                        "message", "Account verified successfully"));
            }
        } catch (RuntimeException | ExecutionException | InterruptedException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Sends a verification email to the specified user.
     *
     * @param email The email address of the user.
     * @return A {@link ResponseEntity} indicating the result of the email-sending process.
     */
    @PostMapping("/send-verification-email")
    public ResponseEntity<?> sendVerificationEmail(@RequestBody String email) {
        try {
            // Retrieve the UserDTO by email
            UserDTO user = userService.getUser(userService.getUserIdByEmail(email));
            
            // Pass the UserDTO to sendVerificationEmail
            authenticationService.sendVerificationEmail(user);

            return ResponseEntity.ok("Verification code sent");
        } catch (RuntimeException | ExecutionException | InterruptedException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Resends the verification code to the specified user.
     *
     * @param email The email address of the user.
     * @return A {@link ResponseEntity} indicating the result of the resend operation.
     */
    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent");
        } catch (RuntimeException | ExecutionException | InterruptedException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
