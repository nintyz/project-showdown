package com.yourpackage.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ExactPlayersValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExactPlayers {

    // Default error message
    String message() default "The tournament must have exactly 32 players";

    // These two attributes are required for custom annotations
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}