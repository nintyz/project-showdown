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

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
    @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter")
    @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit")
    @Pattern(regexp = ".*[@#$%^&+=].*", message = "Password must contain at least one special character (@#$%^&+=)")
    private String password;

    @NotBlank(message = "Role is mandatory")
    @Pattern(regexp = "^(admin|player)$", message = "Role must be either 'admin' or 'player'")
    private String role;

    private String twoFactorSecret;

    private Player playerDetails;

    public User(String email, String password, String role, Player playerDetails) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.playerDetails = playerDetails;
    }

    public User(String email, String password, String role, String twoFactorSecret, Player playerDetails) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.twoFactorSecret = twoFactorSecret;
        this.playerDetails = playerDetails;
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
        return true;
    }

}