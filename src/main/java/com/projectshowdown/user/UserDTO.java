package com.projectshowdown.user;

public class UserDTO {
    private String email;
    private String password;
    private String role;
    private Player playerDetails;

    public UserDTO(String email, String password, String role, Player playerDetails) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.playerDetails = playerDetails;
    }

    public UserDTO() {}

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
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

    public Player getPlayerDetails() {
        return playerDetails;
    }

    public void setPlayerDetails(Player playerDetails) {
        this.playerDetails = playerDetails;
    }
}
