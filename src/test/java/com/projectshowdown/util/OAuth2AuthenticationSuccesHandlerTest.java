package com.projectshowdown.util;

import com.projectshowdown.configs.JwtUtil;
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
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.RedirectStrategy;

import java.io.IOException;

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
    void onAuthenticationSuccess_ShouldRedirectToOAuth2Redirect() throws IOException {
        // Arrange
        String email = "test@example.com";
        String token = "dummy-token";

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn(email);
        when(userService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn(token);
        when(request.getSession()).thenReturn(session);
        when(response.isCommitted()).thenReturn(false);

        // Act
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        verify(session).setAttribute("token", token);
        verify(redirectStrategy).sendRedirect(request, response, "/oauth2/redirect");
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