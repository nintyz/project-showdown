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
        // Initialize Validator for testing constraints
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // Initialize a user with valid values
        user = new User("test@example.com", "Password1@", "player", null);
    }

    @Test
    public void testValidUser() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "There should be no violations for a valid user.");
    }

    @Test
    public void testInvalidEmail() {
        user.setEmail("invalid-email"); // Invalid email format
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "There should be a violation for an invalid email.");
    }

    @Test
    public void testEmptyEmail() {
        user.setEmail(""); // Empty email
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "There should be a violation for an empty email.");
    }

    @Test
    public void testPasswordTooShort() {
        user.setPassword("Short1@"); // Password too short
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "There should be a violation for a password that is too short.");
    }

    @Test
    public void testPasswordMissingUppercase() {
        user.setPassword("password1@"); // Missing uppercase letter
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "There should be a violation for a password missing an uppercase letter.");
    }

    @Test
    public void testPasswordMissingDigit() {
        user.setPassword("Password@"); // Missing digit
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "There should be a violation for a password missing a digit.");
    }

    @Test
    public void testPasswordMissingSpecialCharacter() {
        user.setPassword("Password1"); // Missing special character
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "There should be a violation for a password missing a special character.");
    }

    @Test
    public void testInvalidRole() {
        user.setRole("invalidRole"); // Invalid role
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "There should be a violation for an invalid role.");
    }

    @Test
    public void testValidRole() {
        user.setRole("admin"); // Valid role
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "There should be no violations for a valid role.");
    }

    @Test
    public void testUserDetailsMethods() {
        // Test for getUsername
        assertEquals("test@example.com", user.getUsername(), "The username should match the email.");

        // Test for getPassword
        assertEquals("Password1@", user.getPassword(), "The password should match the expected value.");

        // Test for getAuthorities
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertEquals(1, authorities.size(), "There should be one authority.");
        assertEquals("player", authorities.iterator().next().getAuthority(), "The authority should match the user's role.");

        // Test for account status methods
        assertTrue(user.isAccountNonExpired(), "The account should not be expired.");
        assertTrue(user.isAccountNonLocked(), "The account should not be locked.");
        assertTrue(user.isCredentialsNonExpired(), "The credentials should not be expired.");
        assertTrue(user.isEnabled(), "The account should be enabled.");
    }
}
