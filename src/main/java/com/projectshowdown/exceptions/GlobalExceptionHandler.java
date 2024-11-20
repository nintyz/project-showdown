package com.projectshowdown.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for handling exceptions across the entire application.
 * This class provides a centralized way to manage and return meaningful error responses to the client.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles exceptions caused by validation errors in method arguments.
     *
     * @param ex The exception that occurred, typically due to invalid input in a request body or parameters.
     * @return A ResponseEntity containing a meaningful error message and a BAD_REQUEST HTTP status.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Extract the first validation error message from the exception
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        // Return a 400 Bad Request response with the error message
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
