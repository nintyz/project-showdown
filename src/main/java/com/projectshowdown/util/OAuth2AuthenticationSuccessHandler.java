package com.projectshowdown.util;

import com.projectshowdown.configs.JwtUtil;
import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.service.AuthenticationService;
import com.projectshowdown.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    UserService userService;

    @Autowired
    JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        if (response.isCommitted()) {
            return;
        }

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        String frontendUrl = "http://localhost:3000";

        try {
            UserDTO user = userService.getUser(userService.getUserIdByEmail(email));

            if (!user.isEnabled()) {
                // User needs verification
                authenticationService.resendVerificationCode(email);
                request.getSession().setAttribute("email", email);
                request.getSession().setAttribute("status", "UNVERIFIED");
                getRedirectStrategy().sendRedirect(request, response,
                        frontendUrl + "/verify?email=" + email);
                return;
            }

            UserDetails userDetails = userService.loadUserByUsername(email);

            if (user.getTwoFactorSecret() != null) {
                // User has 2FA enabled
                request.getSession().setAttribute("email", email);
                request.getSession().setAttribute("userId", user.getId());
                request.getSession().setAttribute("status", "REQUIRES_2FA");
                getRedirectStrategy().sendRedirect(request, response,
                        frontendUrl + "/verify-2fa?email=" + email);
            } else {
                // No 2FA, generate token
                String token = jwtUtil.generateToken(userDetails);
                getRedirectStrategy().sendRedirect(request, response,
                        frontendUrl + "/oauth2/callback?token=" + token);
            }
        } catch (UsernameNotFoundException e) {
            // User not found
            getRedirectStrategy().sendRedirect(request, response,
                    frontendUrl + "/signup?email=" + email + "&oauth=true");

        } catch (Exception e) {
            getRedirectStrategy().sendRedirect(request, response,
                    frontendUrl + "/login?error=authentication_failed");
        }
    }
}



