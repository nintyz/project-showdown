package com.projectshowdown.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.projectshowdown.entities.Player;

import java.util.List;

public class ExactPlayersValidator implements ConstraintValidator<ExactPlayers, List<Player>> {
    @Override
    public void initialize(ExactPlayers constraintAnnotation) {}

    @Override
    public boolean isValid(List<Player> players, ConstraintValidatorContext context) {
        return players != null && players.size() == 32;
    }
}