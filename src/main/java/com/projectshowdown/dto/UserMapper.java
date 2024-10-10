package com.projectshowdown.dto;

import com.projectshowdown.entities.User;

//Purpose of this file is to convert our local User object into a special userDTO object
// so it does not store redundent methods into our firebase.
public class UserMapper {
    public static UserDTO toUserDTO(User user) {
        return new UserDTO(user.getId(), user.getEmail(), user.getPassword(), user.getRole(), user.getPlayerDetails());
    }

    public static User toUser(UserDTO userDTO) {
        return new User(userDTO.getEmail(), userDTO.getPassword(), userDTO.getRole(), userDTO.getPlayerDetails());
    }
}
