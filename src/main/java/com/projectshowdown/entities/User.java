package com.projectshowdown.entities;

import java.util.Collection;
import java.util.Collections;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Email;

/**
 * Represents a user in the system.
 * Implements {@link UserDetails} to provide user authentication and authorization details to Spring Security.
 */

@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {

    /**
     * The unique identifier for the user.
     */
    private String id;

    /**
     * The user's name.
     * Must be between 2 and 50 characters.
     */
    @NotNull(message = "Name is mandatory")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    /**
     * The URL to the user's profile picture.
     */
    private String profileUrl;

    /**
     * The user's email address.
     * Must be a valid email format.
     */
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    /**
     * The user's password.
     * Must meet security requirements such as minimum length, uppercase and lowercase letters, digits, and special characters.
     */
    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
    @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter")
    @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit")
    @Pattern(regexp = ".*[!@#$%^&+=].*", message = "Password must contain at least one special character (!@#$%^&+=)")
    private String password;

    /**
     * The user's role (e.g., "player", "organizer", or "admin").
     * Must match one of the predefined roles.
     */
    @NotBlank(message = "Role is mandatory")
    @Pattern(regexp = "^(admin|player|organizer)$", message = "Role must be either player/organizer")
    private String role;

    /**
     * The secret key for two-factor authentication (2FA).
     */
    private String twoFactorSecret;

    /**
     * The player's details, if the user is a player.
     */
    private Player playerDetails;

    /**
     * The organizer's details, if the user is an organizer.
     */
    private Organizer organizerDetails;

    /**
     * The verification code sent to the user for account verification.
     */
    private String verificationCode;

    /**
     * The timestamp indicating when the verification code expires.
     */
    private Long verificationCodeExpiresAt;

    /**
     * Indicates whether the user's account is enabled.
     */
    private boolean enabled;

    /**
     * Constructs a new User instance with the provided details.
     *
     * @param id                     The unique identifier for the user.
     * @param name                   The user's name.
     * @param profileUrl             The URL to the user's profile picture.
     * @param email                  The user's email address.
     * @param password               The user's password.
     * @param role                   The user's role (e.g., "player", "organizer", or "admin").
     * @param twoFactorSecret        The secret key for two-factor authentication.
     * @param playerDetails          The player's details, if applicable.
     * @param organizerDetails       The organizer's details, if applicable.
     * @param verificationCode       The verification code for account verification.
     * @param verificationCodeExpiresAt The timestamp indicating when the verification code expires.
     * @param enabled                Whether the user's account is enabled.
     */
    public User(String id,
            @NotNull(message = "Name is mandatory") @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters") String name,
            String profileUrl,
            @NotBlank(message = "Email is mandatory") @Email(message = "Email should be valid") String email,
            @NotBlank(message = "Password is mandatory") @Size(min = 8, message = "Password must be at least 8 characters long") @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter") @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter") @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit") @Pattern(regexp = ".*[!@#$%^&+=].*", message = "Password must contain at least one special character (!@#$%^&+=)") String password,
            @NotBlank(message = "Role is mandatory") @Pattern(regexp = "^(admin|player|organizer)$", message = "Role must be either player/organizer") String role,
            String twoFactorSecret, Player playerDetails, Organizer organizerDetails, String verificationCode,
            Long verificationCodeExpiresAt, boolean enabled) {
        this.id = id;
        this.name = name;
        this.profileUrl = profileUrl;
        this.email = email;
        this.password = password;
        this.role = role;
        this.twoFactorSecret = twoFactorSecret;
        this.playerDetails = playerDetails;
        this.organizerDetails = organizerDetails;
        this.verificationCode = verificationCode;
        this.verificationCodeExpiresAt = verificationCodeExpiresAt;
        this.enabled = enabled;
    }

    /**
     * Returns the username for Spring Security authentication.
     *
     * @return The user's email address as the username.
     */
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * Returns the authorities granted to the user.
     *
     * @return A collection of the user's authorities (roles).
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Return the role as a SimpleGrantedAuthority
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

}