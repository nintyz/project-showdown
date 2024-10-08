package com.projectshowdown.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class PlayerNotFoundException extends RuntimeException{
    public PlayerNotFoundException(String id) {
        super("Could not find Player " + id);
    }
}



