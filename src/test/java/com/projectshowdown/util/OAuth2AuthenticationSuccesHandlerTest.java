package com.projectshowdown.util;

import com.projectshowdown.configs.JwtUtil;
import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.service.AuthenticationService;
import com.projectshowdown.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.RedirectStrategy;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

class OAuth2AuthenticationSuccessHandlerTest {

    @InjectMocks
    private OAuth2AuthenticationSuccessHandler successHandler;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private OAuth2User oAuth2User;

    @Mock
    private UserDetails userDetails;

    @Mock
    private HttpSession session;

    @Mock
    private RedirectStrategy redirectStrategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        successHandler.setRedirectStrategy(redirectStrategy);
    }

    @Test
    void onAuthenticationSuccess_WhenUserUnverified_ShouldRedirectToVerify() throws IOException, ExecutionException, InterruptedException {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("test@example.com");
        when(userService.getUserIdByEmail("test@example.com")).thenReturn("userId");

        UserDTO userDTO = new UserDTO();
        userDTO.setEnabled(false);
        when(userService.getUser("userId")).thenReturn(userDTO);
        when(request.getSession()).thenReturn(session);

        // Act
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(session).setAttribute("email", "test@example.com");
        verify(session).setAttribute("status", "UNVERIFIED");
        verify(redirectStrategy).sendRedirect(request, response,
                "http://localhost:3000/verify?email=test@example.com");
    }

    @Test
    void onAuthenticationSuccess_WhenUserHas2FA_ShouldRedirectToVerify2FA() throws IOException, ExecutionException, InterruptedException {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("test@example.com");
        when(userService.getUserIdByEmail("test@example.com")).thenReturn("userId");

        UserDTO userDTO = new UserDTO();
        userDTO.setEnabled(true);
        userDTO.setId("userId");
        userDTO.setTwoFactorSecret("secret");
        when(userService.getUser("userId")).thenReturn(userDTO);
        when(request.getSession()).thenReturn(session);

        // Act
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(session).setAttribute("email", "test@example.com");
        verify(session).setAttribute("userId", "userId");
        verify(session).setAttribute("status", "REQUIRES_2FA");
        verify(redirectStrategy).sendRedirect(request, response,
                "http://localhost:3000/verify-2fa?email=test@example.com");
    }

    @Test
    void onAuthenticationSuccess_WhenUserNotFound_ShouldRedirectToSignup() throws IOException, ExecutionException, InterruptedException {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("test@example.com");
        when(userService.getUserIdByEmail("test@example.com"))
                .thenThrow(new UsernameNotFoundException("User not found"));

        // Act
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(redirectStrategy).sendRedirect(request, response,
                "http://localhost:3000/signup?email=test@example.com&oauth=true");
    }

    @Test
    void onAuthenticationSuccess_WhenGeneralException_ShouldRedirectToLoginWithError() throws IOException, ExecutionException, InterruptedException {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("test@example.com");
        when(userService.getUserIdByEmail("test@example.com"))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(redirectStrategy).sendRedirect(request, response,
                "http://localhost:3000/login?error=authentication_failed");
    }

    @Test
    void onAuthenticationSuccess_WhenValidUserWithoutTwoFactor_ShouldRedirectWithToken() throws IOException, ExecutionException, InterruptedException {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("test@example.com");
        when(userService.getUserIdByEmail("test@example.com")).thenReturn("userId");

        UserDTO userDTO = new UserDTO();
        userDTO.setEnabled(true);
        userDTO.setTwoFactorSecret(null);
        when(userService.getUser("userId")).thenReturn(userDTO);
        when(userService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("test-jwt-token");

        // Act
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(redirectStrategy).sendRedirect(request, response,
                "http://localhost:3000/oauth2/callback?token=test-jwt-token");
    }

    @Test
    void onAuthenticationSuccess_WhenResponseIsCommitted_ShouldNotRedirect() throws IOException {
        // Arrange
        when(response.isCommitted()).thenReturn(true);

        // Act
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(redirectStrategy, never()).sendRedirect(any(), any(), anyString());
    }
}