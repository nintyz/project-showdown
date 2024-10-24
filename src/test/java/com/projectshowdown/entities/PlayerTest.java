package com.projectshowdown.entities;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class PlayerTest {

    private Player player;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // Arrange
        player = new Player(1, "John Doe", LocalDate.now().toString(), 24, 2000.0, 2500.0, 500.0, 400.0, 300.0);
    }

    @Test
    public void testPlayerNameTooShort() {
        // Act
        player.setName("A");

        // Assert
        Set<ConstraintViolation<Player>> violations = validator.validate(player);
        assertFalse(violations.isEmpty(), "There should be a violation for the player's name being too short.");
    }

    @Test
    public void testPlayerNameValid() {
        // Act
        player.setName("John Doe");

        // Assert
        Set<ConstraintViolation<Player>> violations = validator.validate(player);
        assertTrue(violations.isEmpty(), "There should be no violations for a valid name.");
    }

    @Test
    public void testPlayerAgeNegative() {
        // Act
        player.setDob(LocalDate.now().plusYears(5).toString());

        // Assert
        Set<ConstraintViolation<Player>> violations = validator.validate(player);
        assertFalse(violations.isEmpty(), "There should be a violation for a negative age.");
    }

    @Test
    public void testPlayerAgeExceed() {
        // Act
        player.setDob(LocalDate.now().minusYears(200).toString());

        // Assert
        Set<ConstraintViolation<Player>> violations = validator.validate(player);
        assertFalse(violations.isEmpty(), "There should be a violation for age exceeding the max limit.");
    }

    @Test
    public void testPlayerAgeValid() {
        // Act
        player.setDob(LocalDate.now().minusYears(26).toString());

        // Assert
        Set<ConstraintViolation<Player>> violations = validator.validate(player);
        assertTrue(violations.isEmpty(), "There should be no violations for a valid age.");
    }

    @Test
    public void testPlayerEloNegative() {
        // Act
        player.setElo(-100);

        // Assert
        Set<ConstraintViolation<Player>> violations = validator.validate(player);
        assertFalse(violations.isEmpty(), "There should be a violation for negative Elo.");
    }

    @Test
    public void testPlayerEloValid() {
        // Act
        player.setElo(2000.0);

        // Assert
        Set<ConstraintViolation<Player>> violations = validator.validate(player);
        assertTrue(violations.isEmpty(), "There should be no violations for a valid Elo.");
    }

    @Test
    public void testPlayerPeakEloNegative() {
        // Act
        player.setPeakElo(-50);

        // Assert
        Set<ConstraintViolation<Player>> violations = validator.validate(player);
        assertFalse(violations.isEmpty(), "There should be a violation for negative Peak Elo.");
    }

    @Test
    public void testPlayerPeakEloValid() {
        // Act
        player.setPeakElo(2500.0);

        // Assert
        Set<ConstraintViolation<Player>> violations = validator.validate(player);
        assertTrue(violations.isEmpty(), "There should be no violations for a valid Peak Elo.");
    }

    @Test
    public void testPlayerRawScoreNegative() {
        // Act
        player.setHardRaw(-1);

        // Assert
        Set<ConstraintViolation<Player>> violations = validator.validate(player);
        assertFalse(violations.isEmpty(), "There should be a violation for negative hard raw score.");
    }

    @Test
    public void testPlayerRawScoreValid() {
        // Act
        player.setHardRaw(500.0);

        // Assert
        Set<ConstraintViolation<Player>> violations = validator.validate(player);
        assertTrue(violations.isEmpty(), "There should be no violations for a valid hard raw score.");
    }

    @Test
    public void testCalculateMMR() {
        // Act
        player.setElo(2000);
        player.setPeakElo(2500);
        player.setDob(LocalDate.now().minusYears(25).toString());
        player.setPeakAge(24);

        // Assert
        double expectedMMR = 2000 + 2500 * Math.cos((25 - 24) * Math.PI / 10) + 1000;
        double actualMMR = player.calculateMMR();
        assertEquals(expectedMMR, actualMMR, 0.0001, "The calculated MMR should match the expected value.");
    }

}