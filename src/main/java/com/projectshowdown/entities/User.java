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

/* Implementations of UserDetails to provide user information to Spring Security, 
e.g., what authorities (roles) are granted to the user and whether the account is enabled or not
*/

@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {

    private String id;

    @NotNull(message = "Name is mandatory")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    private String profileUrl;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
    @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter")
    @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit")
    @Pattern(regexp = ".*[!@#$%^&+=].*", message = "Password must contain at least one special character (!@#$%^&+=)")
    private String password;

    @NotBlank(message = "Role is mandatory")
    @Pattern(regexp = "^(admin|player|organizer)$", message = "Role must be either player/organizer")
    private String role;

    private String twoFactorSecret;

    private Player playerDetails;
    private Organizer organizerDetails;

    private String verificationCode;
    private Long verificationCodeExpiresAt;
    private boolean enabled;

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

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTwoFactorSecret() {
        return twoFactorSecret;
    }

    public void setTwoFactorSecret(String twoFactorSecret) {
        this.twoFactorSecret = twoFactorSecret;
    }

    public Player getPlayerDetails() {
        return playerDetails;
    }

    public void setPlayerDetails(Player playerDetails) {
        this.playerDetails = playerDetails;
    }

    // Getter for organizerDetails
    public Organizer getOrganizerDetails() {
        return organizerDetails;
    }

    // Setter for organizerDetails
    public void setOrganizerDetails(Organizer organizerDetails) {
        this.organizerDetails = organizerDetails;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

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