package com.projectshowdown.dto;

import com.projectshowdown.entities.User;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

//Purpose of this file is to convert our local User object into a special userDTO object
// so it does not store redundent methods into our firebase.
public class UserMapper {
    public static UserDTO toUserDTO(User user) {
        return new UserDTO(user.getId(), user.getEmail(), user.getPassword(), user.getRole(), user.getTwoFactorSecret(),
                user.getPlayerDetails(), user.getOrganizerDetails(), user.getVerificationCode(), user.getVerificationCodeExpiresAt(),
                user.isEnabled());
    }

    public static User toUser(UserDTO userDTO) {
        return new User(userDTO.getId(), userDTO.getEmail(), userDTO.getPassword(), userDTO.getRole(),
                userDTO.getTwoFactorSecret(), userDTO.getPlayerDetails(), userDTO.getVerificationCode(),
                userDTO.getVerificationCodeExpiresAt(), userDTO.isEnabled());
    }

    public static Map<String, Object> toMap(UserDTO user) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(user, Map.class);
    }
}
