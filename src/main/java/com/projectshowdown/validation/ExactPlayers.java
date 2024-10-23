package com.projectshowdown.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ExactPlayersValidator.class)
@Target({ ElementType.TYPE })  // Target the entire class (Tournament)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExactPlayers {
    String message() default "The number of players must match the required number for this tournament";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}