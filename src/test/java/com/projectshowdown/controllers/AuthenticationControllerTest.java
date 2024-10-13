package com.projectshowdown.controllers;

import com.projectshowdown.configs.JwtUtil;
import com.projectshowdown.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loginRedirect_withValidToken_returnsToken() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("token")).thenReturn("valid_token");

        ResponseEntity<?> response = authenticationController.loginRedirect(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("valid_token", responseBody.get("token"));

        verify(session).removeAttribute("token");
        verify(session).invalidate();
    }

    @Test
    void loginRedirect_withNoSession_returnsUnauthorized() {
        when(request.getSession(false)).thenReturn(null);

        ResponseEntity<?> response = authenticationController.loginRedirect(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorised", response.getBody());
    }

    @Test
    void createAuthenticationToken_withValidCredentials_returnsToken() throws Exception {
        User userCredentials = new User();
        userCredentials.setEmail("test@example.com");
        userCredentials.setPassword("password");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("generated_token");

        Map<String, String> response = authenticationController.createAuthenticationToken(userCredentials);

        assertNotNull(response);
        assertEquals("generated_token", response.get("token"));

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("test@example.com", "password"));
    }

    @Test
    void createAuthenticationToken_withNonExistentUser_throwsException() {
        User userCredentials = new User();
        userCredentials.setEmail("nonexistent@example.com");
        userCredentials.setPassword("password");

        when(userDetailsService.loadUserByUsername("nonexistent@example.com"))
                .thenThrow(new UsernameNotFoundException("User not found"));

        Exception exception = assertThrows(Exception.class, () ->
                authenticationController.createAuthenticationToken(userCredentials)
        );

        assertEquals("Account with the specified email does not exist", exception.getMessage());
    }

    @Test
    void createAuthenticationToken_withIncorrectPassword_throwsException() {
        User userCredentials = new User();
        userCredentials.setEmail("test@example.com");
        userCredentials.setPassword("wrong_password");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        Exception exception = assertThrows(Exception.class, () ->
                authenticationController.createAuthenticationToken(userCredentials)
        );

        assertEquals("Incorrect password", exception.getMessage());
    }
}