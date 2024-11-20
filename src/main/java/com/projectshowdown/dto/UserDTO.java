package com.projectshowdown.dto;

import com.projectshowdown.entities.Organizer;
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
    private String name;
    private String profileUrl;
    private String email;
    private String password;
    private String role;
    private String twoFactorSecret;
    private Player playerDetails;
    private Organizer organizerDetails;
    private String verificationCode;
    private Long verificationCodeExpiresAt;
    private boolean enabled;

}
