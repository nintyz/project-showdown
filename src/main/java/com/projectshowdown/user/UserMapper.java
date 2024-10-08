package com.projectshowdown.user;

import com.projectshowdown.dto.UserDTO;

//Purpose of this file is to convert our local User object into a special userDTO object
// so it does not store redundent methods into our firebase.
public class UserMapper {
    public static UserDTO toUserDTO(User user) {
        return new UserDTO(Integer.toString(user.getId()), user.getEmail(), user.getPassword(), user.getRole(), user.getPlayerDetails());
    }

    public static User toUser(UserDTO userDTO) {
        return new User(userDTO.getEmail(), userDTO.getPassword(), userDTO.getRole(), userDTO.getPlayerDetails());
    }
}
