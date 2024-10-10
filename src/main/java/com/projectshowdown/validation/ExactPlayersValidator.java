package com.yourpackage.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class ExactPlayersValidator implements ConstraintValidator<ExactPlayers, List<?>> {

    private static final int REQUIRED_SIZE = 32; // Define the exact number of players allowed

    @Override
    public void initialize(ExactPlayers constraintAnnotation) {
        // No initialization needed since the number of players is fixed
    }

    @Override
    public boolean isValid(List<?> players, ConstraintValidatorContext context) {
        if (players == null) {
            return false; // Null list is not valid, we need exactly 32 players
        }
        return players.size() == REQUIRED_SIZE; // Validate if the list size is exactly 32
    }
}