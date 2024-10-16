package com.projectshowdown.entities;

import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class UserTest {

    private User user;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // Arrange
        user = new User("test@example.com", "Password1@", "player", null);
    }

    @Test
    public void testValidUser() {
        // Assert
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "There should be no violations for a valid user.");
    }

    @Test
    public void testInvalidEmail() {
        // Act
        user.setEmail("invalid-email"); // Invalid email format

        // Assert
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "There should be a violation for an invalid email.");
    }

    @Test
    public void testEmptyEmail() {
        // Act
        user.setEmail(""); // Empty email

        // Assert
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "There should be a violation for an empty email.");
    }

    @Test
    public void testPasswordTooShort() {
        // Act
        user.setPassword("Short1@"); // Password too short

        // Assert
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "There should be a violation for a password that is too short.");
    }

    @Test
    public void testPasswordMissingUppercase() {
        // Act
        user.setPassword("password1@"); // Missing uppercase letter

        // Assert
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "There should be a violation for a password missing an uppercase letter.");
    }

    @Test
    public void testPasswordMissingDigit() {
        // Act
        user.setPassword("Password@"); // Missing digit

        // Assert
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "There should be a violation for a password missing a digit.");
    }

    @Test
    public void testPasswordMissingSpecialCharacter() {
        // Act
        user.setPassword("Password1"); // Missing special character
        
        // Assert
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "There should be a violation for a password missing a special character.");
    }

    @Test
    public void testInvalidRole() {
        // Act
        user.setRole("invalidRole"); // Invalid role

        // Assert
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "There should be a violation for an invalid role.");
    }

    @Test
    public void testValidRole() {
        // Act
        user.setRole("admin"); // Valid role

        // Assert
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "There should be no violations for a valid role.");
    }

    @Test
    public void testUserDetailsMethods() {
        // Assert
        assertEquals("test@example.com", user.getUsername(), "The username should match the email.");

        assertEquals("Password1@", user.getPassword(), "The password should match the expected value.");

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertEquals(1, authorities.size(), "There should be one authority.");
        assertEquals("player", authorities.iterator().next().getAuthority(), "The authority should match the user's role.");

        assertTrue(user.isAccountNonExpired(), "The account should not be expired.");
        assertTrue(user.isAccountNonLocked(), "The account should not be locked.");
        assertTrue(user.isCredentialsNonExpired(), "The credentials should not be expired.");
        assertTrue(user.isEnabled(), "The account should be enabled.");
    }
}
