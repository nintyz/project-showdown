package com.projectshowdown.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception to handle cases where a tournament is not found in the system.
 * This exception triggers a {@link HttpStatus#NOT_FOUND} (404) response.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TournamentNotFoundException extends RuntimeException{
    
    /**
     * Constructs a new TournamentNotFoundException with a detailed error message.
     *
     * @param id The ID of the tournament that could not be found.
     */
    public TournamentNotFoundException(String id) {
        super("Could not find Tournament " + id);
    }
}



