package com.projectshowdown.user;

public class UserMapper {
    public static UserDTO toUserDTO(User user) {
        return new UserDTO(user.getEmail(), user.getPassword(), user.getRole(), user.getPlayerDetails());
    }

    public static User toUser(UserDTO userDTO) {
        return new User(userDTO.getEmail(), userDTO.getPassword(), userDTO.getRole(), userDTO.getPlayerDetails());
    }
}

