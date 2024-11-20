package com.projectshowdown.dto;

import com.projectshowdown.entities.User;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Utility class for mapping between {@link User} and into special {@link UserDTO} objects.
 * Provides methods for converting user data to ensure only relevant fields are transferred or stored into Firebase.
 */
public class UserMapper {
    /**
     * Converts a {@link User} entity to a {@link UserDTO}.
     *
     * @param user The {@link User} entity to convert.
     * @return A {@link UserDTO} containing the relevant user details.
     */
    public static UserDTO toUserDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getProfileUrl(), user.getEmail(), user.getPassword(),
                user.getRole(), user.getTwoFactorSecret(),
                user.getPlayerDetails(), user.getOrganizerDetails(), user.getVerificationCode(),
                user.getVerificationCodeExpiresAt(),
                user.isEnabled());
    }

    /**
     * Converts a {@link UserDTO} to a {@link User} entity.
     *
     * @param userDTO The {@link UserDTO} to convert.
     * @return A {@link User} entity containing all fields from the {@link UserDTO}.
     */
    public static User toUser(UserDTO userDTO) {
        return new User(userDTO.getId(), userDTO.getName(), userDTO.getProfileUrl(), userDTO.getEmail(),
                userDTO.getPassword(), userDTO.getRole(),
                userDTO.getTwoFactorSecret(), userDTO.getPlayerDetails(), userDTO.getOrganizerDetails(),
                userDTO.getVerificationCode(), userDTO.getVerificationCodeExpiresAt(), userDTO.isEnabled());
    }

    /**
     * Converts a {@link UserDTO} to a {@link Map} representation.
     *
     * @param user The {@link UserDTO} to convert.
     * @return A {@link Map} containing key-value pairs of the user's fields.
     */
    public static Map<String, Object> toMap(UserDTO user) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(user, Map.class);
    }
}
