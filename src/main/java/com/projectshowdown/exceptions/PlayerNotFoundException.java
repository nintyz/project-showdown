package com.projectshowdown.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class PlayerNotFoundException extends RuntimeException{
    
    private static final long serialVersionUID = 1L;

    public PlayerNotFoundException(int id) {
        super("Could not find Player " + id);
    }
}



