package com.projectshowdown.dto;

import com.projectshowdown.entities.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//special User object to ensure firebase only store the variables here.
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String id;
    private String email;
    private String password;
    private String role;
    private String twoFactorSecret;
    private Player playerDetails;

    // Default constructor
    public UserDTO() {

    }

    // Constructor with parameters
    public UserDTO(String id, String email, String password, String role, String twoFactorSecret, Player playerDetails) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.twoFactorSecret = twoFactorSecret;
        this.playerDetails = playerDetails;
    }

    // Getter for id
    public String getId() {
        return id;
    }

    // Setter for id
    public void setId(String id) {
        this.id = id;
    }

    // Getter for email
    public String getEmail() {
        return email;
    }

    // Setter for email
    public void setEmail(String email) {
        this.email = email;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    // Setter for password
    public void setPassword(String password) {
        this.password = password;
    }

    // Getter for role
    public String getRole() {
        return role;
    }

    // Setter for role
    public void setRole(String role) {
        this.role = role;
    }

    // Getter for twoFactorSecret
    public String getTwoFactorSecret() {
        return twoFactorSecret;
    }

    // Setter for twoFactorSecret
    public void setTwoFactorSecret(String twoFactorSecret) {
        this.twoFactorSecret = twoFactorSecret;
    }

    // Getter for playerDetails
    public Player getPlayerDetails() {
        return playerDetails;
    }

    // Setter for playerDetails
    public void setPlayerDetails(Player playerDetails) {
        this.playerDetails = playerDetails;
    }
}
