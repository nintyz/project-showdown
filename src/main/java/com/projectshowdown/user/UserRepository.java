package com.projectshowdown.user;

import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository{
    // define a derived query to find user by username
    Optional<User> findByUsername(String username);
}