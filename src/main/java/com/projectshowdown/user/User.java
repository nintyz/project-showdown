package com.projectshowdown.user;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/* Implementations of UserDetails to provide user information to Spring Security, 
e.g., what authorities (roles) are granted to the user and whether the account is enabled or not
*/
public class User implements UserDetails {
    private String id;
    private String email;
    private String password;
    private String role;
    private Player playerDetails;

    public User(String email, String password, String role, Player playerDetails) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.playerDetails = playerDetails;
    }

    public User() {

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

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Player getPlayerDetails() {
        return playerDetails;
    }

    public void setPlayerDetails(Player playerDetails) {
        this.playerDetails = playerDetails;
    }
}