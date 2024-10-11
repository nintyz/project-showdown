package com.projectshowdown.entities;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class PlayerTest {

    private Player player;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        // Initialize Validator for testing constraints
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // Initialize a player with default values
        player = new Player("iEb40SaFffPxcpkMgoGO", 1, "John Doe", 25, 24, 2000.0, 2500.0, 500.0, 400.0, 300.0);
    }

    @Test
    public void testPlayerNameValidation() {
        player.setName("A"); // Too short name

        Set<ConstraintViolation<Player>> violations = validator.validate(player);
        assertFalse(violations.isEmpty(), "There should be a violation for the player's name being too short.");

        player.setName("John Doe");
        violations = validator.validate(player);
        assertTrue(violations.isEmpty(), "There should be no violations for a valid name.");
    }

    @Test
    public void testPlayerAgeValidation() {
        player.setAge(-5); // Invalid age
        Set<ConstraintViolation<Player>> violations = validator.validate(player);
        assertFalse(violations.isEmpty(), "There should be a violation for a negative age.");

        player.setAge(121); // Invalid age
        violations = validator.validate(player);
        assertFalse(violations.isEmpty(), "There should be a violation for age exceeding the max limit.");

        player.setAge(25); // Valid age
        violations = validator.validate(player);
        assertTrue(violations.isEmpty(), "There should be no violations for a valid age.");
    }

    @Test
    public void testPlayerEloValidation() {
        player.setElo(-100); // Invalid Elo
        Set<ConstraintViolation<Player>> violations = validator.validate(player);
        assertFalse(violations.isEmpty(), "There should be a violation for negative Elo.");

        player.setElo(2000.0); // Valid Elo
        violations = validator.validate(player);
        assertTrue(violations.isEmpty(), "There should be no violations for a valid Elo.");
    }

    @Test
    public void testPlayerPeakEloValidation() {
        player.setPeakElo(-50); // Invalid Peak Elo
        Set<ConstraintViolation<Player>> violations = validator.validate(player);
        assertFalse(violations.isEmpty(), "There should be a violation for negative Peak Elo.");

        player.setPeakElo(2500.0); // Valid Peak Elo
        violations = validator.validate(player);
        assertTrue(violations.isEmpty(), "There should be no violations for a valid Peak Elo.");
    }

    @Test
    public void testPlayerRawScoresValidation() {
        player.setHardRaw(-1); // Invalid hard raw score
        Set<ConstraintViolation<Player>> violations = validator.validate(player);
        assertFalse(violations.isEmpty(), "There should be a violation for negative hard raw score.");

        player.setHardRaw(500.0); // Valid hard raw score
        violations = validator.validate(player);
        assertTrue(violations.isEmpty(), "There should be no violations for a valid hard raw score.");
    }

    @Test
    public void testCalculateMMR() {
        // Set specific values to test the formula
        player.setElo(2000);
        player.setPeakElo(2500);
        player.setAge(25);
        player.setPeakAge(24);

        double expectedMMR = 2000 + 2500 * Math.cos((25 - 24) * Math.PI / 10) + 1000;
        double actualMMR = player.calculateMMR();

        assertEquals(expectedMMR, actualMMR, 0.0001, "The calculated MMR should match the expected value.");
    }

}