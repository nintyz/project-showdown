package com.projectshowdown.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.dto.VerifyUserDto;
import com.projectshowdown.service.AuthenticationService;
import com.projectshowdown.service.TwoFactorAuthService;
import com.projectshowdown.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/oauth2/redirect")
    public ResponseEntity<?> loginRedirect(HttpServletRequest request) {

        // Get the session, and don't create a new one if it doesn't exist
        HttpSession session = request.getSession(false);

        if (session != null) {
            String token = (String) session.getAttribute("token");
            if (token != null) {
                // Remove the token from the session
                session.removeAttribute("token");

                // Invalidate the session for extra security
                session.invalidate();

                // Create the response
                Map<String, String> responseBody = new HashMap<>();
                responseBody.put("token", token);

                return ResponseEntity.ok(responseBody);
            }
        }

        // If accessed directly, return an unauthorised response
        return new ResponseEntity<>("Unauthorised", HttpStatus.UNAUTHORIZED);
    }

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
                return ResponseEntity.ok(Map.of("token", token, "requiresTwoFactor", false));
            }
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body("Account with the specified email does not exist");
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Incorrect password");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Authentication failed");
        }
    }

    @PostMapping("/enable-2fa")
    public ResponseEntity<?> enableTwoFactorAuth(@RequestParam String email) {
        try {
            String qrCodeImage = userService.enableTwoFactorAuth(userService.getUserIdByEmail(email));
            return ResponseEntity.ok().body(Map.of("qrCodeImage", qrCodeImage));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error enabling 2FA: " + e.getMessage());
        }
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<?> verifyTwoFactorAuth(@RequestParam String email, @RequestParam String code) {
        try {
            UserDTO user = userService.getUser(userService.getUserIdByEmail(email));
            boolean isValid = twoFactorAuthService.verifyCode(user.getTwoFactorSecret(), code);
            if (isValid) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                String token = jwtUtil.generateToken(userDetails);
                return ResponseEntity.ok(Map.of("token", token));
            } else {
                return ResponseEntity.badRequest().body("Invalid 2FA code");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error verifying 2FA code: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        try {
            authenticationService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("Account verified successfully");
        } catch (RuntimeException | ExecutionException | InterruptedException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

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
