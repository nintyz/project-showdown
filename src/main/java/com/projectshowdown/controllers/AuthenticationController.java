package com.projectshowdown.controllers;

import java.util.HashMap;
import java.util.Map;

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
import com.projectshowdown.user.User;

@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

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
    public Map<String, String> createAuthenticationToken(@RequestBody User userCredentials) throws Exception {

        try {
            // Load the user by email to check if the user exists
            UserDetails userDetails = userDetailsService.loadUserByUsername(userCredentials.getEmail());
            // If user is found, attempt to authenticate with the password
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userCredentials.getEmail(),
                    userCredentials.getPassword()));

            // Generate the JWT token in a form of a JSON Object upon login.
            Map<String, String> response = new HashMap<>();
            response.put("token", jwtUtil.generateToken(userDetails));
            return response;

        } catch (UsernameNotFoundException e) {
            // This exception will be thrown when no user with the given email is found
            throw new Exception("Account with the specified email does not exist");
        } catch (BadCredentialsException e) {
            // This exception is thrown when the password is incorrect
            throw new Exception("Incorrect password");
        } catch (Exception e) {
            // Catch any other generic exceptions and propagate them upwards
            throw new Exception("Authentication failed", e);
        }
    }
}
