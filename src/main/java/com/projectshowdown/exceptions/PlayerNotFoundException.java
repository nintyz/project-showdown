package com.projectshowdown.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception to handle cases where a player is not found in the system.
 * This exception triggers a {@link HttpStatus#NOT_FOUND} (404) response.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PlayerNotFoundException extends RuntimeException{
    /**
     * Constructs a new PlayerNotFoundException with a detailed error message.
     *
     * @param id The ID of the player that could not be found.
     */
    public PlayerNotFoundException(String id) {
        super("Could not find Player " + id);
    }
}



