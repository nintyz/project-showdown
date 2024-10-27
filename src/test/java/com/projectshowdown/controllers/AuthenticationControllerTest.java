package com.projectshowdown.controllers;

import com.projectshowdown.configs.JwtUtil;
import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.entities.User;
import com.projectshowdown.service.TwoFactorAuthService;
import com.projectshowdown.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

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
    private UserService userService;

    @Mock
    private TwoFactorAuthService twoFactorAuthService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loginRedirect_withValidSession_returnsToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("token")).thenReturn("testToken");

        ResponseEntity<?> response = authenticationController.loginRedirect(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        assertEquals("testToken", ((Map<?, ?>) response.getBody()).get("token"));
        verify(session).removeAttribute("token");
        verify(session).invalidate();
    }

    @Test
    void loginRedirect_withInvalidSession_returnsUnauthorized() {
        HttpServletRequest request = mock(HttpServletRequest.class);
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
        when(jwtUtil.generateToken(userDetails)).thenReturn("testToken");

        UserDTO userDTO = new UserDTO();
        userDTO.setId("5pEhJtbM2c9w7SwanaPn");
        when(userService.getUserIdByEmail("test@example.com")).thenReturn("5pEhJtbM2c9w7SwanaPn");
        when(userService.getPlayer("5pEhJtbM2c9w7SwanaPn")).thenReturn(userDTO);

        ResponseEntity<?> response = authenticationController.createAuthenticationToken(userCredentials);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        assertEquals("testToken", ((Map<?, ?>) response.getBody()).get("token"));
        assertFalse((Boolean) ((Map<?, ?>) response.getBody()).get("requiresTwoFactor"));
    }

    @Test
    void createAuthenticationToken_withTwoFactorEnabled_returnsRequiresTwoFactor() throws Exception {
        User userCredentials = new User();
        userCredentials.setEmail("test@example.com");
        userCredentials.setPassword("password");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);

        UserDTO userDTO = new UserDTO();
        userDTO.setId("5pEhJtbM2c9w7SwanaPn");
        userDTO.setTwoFactorSecret("secret");
        when(userService.getUserIdByEmail("test@example.com")).thenReturn("5pEhJtbM2c9w7SwanaPn");
        when(userService.getPlayer("5pEhJtbM2c9w7SwanaPn")).thenReturn(userDTO);

        ResponseEntity<?> response = authenticationController.createAuthenticationToken(userCredentials);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        assertTrue((Boolean) ((Map<?, ?>) response.getBody()).get("requiresTwoFactor"));
        assertEquals("5pEhJtbM2c9w7SwanaPn", ((Map<?, ?>) response.getBody()).get("userId"));
    }

    @Test
    void createAuthenticationToken_withInvalidCredentials_throwsException() {
        User userCredentials = new User();
        userCredentials.setEmail("test@example.com");
        userCredentials.setPassword("wrongpassword");

        when(userDetailsService.loadUserByUsername("test@example.com")).thenThrow(new BadCredentialsException("Invalid credentials"));

        ResponseEntity<?> response = authenticationController.createAuthenticationToken(userCredentials);
        assertEquals("Incorrect password", response.getBody());
    }

    @Test
    void enableTwoFactorAuth_success_returnsQrCodeImage() throws Exception {
        when(userService.getUserIdByEmail("test@example.com")).thenReturn("5pEhJtbM2c9w7SwanaPn");
        when(userService.enableTwoFactorAuth("5pEhJtbM2c9w7SwanaPn")).thenReturn("qrCodeImageData");

        ResponseEntity<?> response = authenticationController.enableTwoFactorAuth("test@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        assertEquals("qrCodeImageData", ((Map<?, ?>) response.getBody()).get("qrCodeImage"));
    }

    @Test
    void verifyTwoFactorAuth_validCode_returnsToken() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setId("5pEhJtbM2c9w7SwanaPn");
        userDTO.setTwoFactorSecret("secret");
        when(userService.getUserIdByEmail("test@example.com")).thenReturn("5pEhJtbM2c9w7SwanaPn");
        when(userService.getPlayer("5pEhJtbM2c9w7SwanaPn")).thenReturn(userDTO);
        when(twoFactorAuthService.verifyCode("secret", "123456")).thenReturn(true);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("testToken");

        ResponseEntity<?> response = authenticationController.verifyTwoFactorAuth("test@example.com", "123456");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        assertEquals("testToken", ((Map<?, ?>) response.getBody()).get("token"));
    }

    @Test
    void verifyTwoFactorAuth_invalidCode_returnsBadRequest() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setId("5pEhJtbM2c9w7SwanaPn");
        userDTO.setTwoFactorSecret("secret");
        when(userService.getUserIdByEmail("test@example.com")).thenReturn("5pEhJtbM2c9w7SwanaPn");
        when(userService.getPlayer("5pEhJtbM2c9w7SwanaPn")).thenReturn(userDTO);
        when(twoFactorAuthService.verifyCode("secret", "123456")).thenReturn(false);

        ResponseEntity<?> response = authenticationController.verifyTwoFactorAuth("test@example.com", "123456");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid 2FA code", response.getBody());
    }
}