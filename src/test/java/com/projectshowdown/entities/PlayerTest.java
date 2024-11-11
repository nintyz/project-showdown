package com.projectshowdown.entities;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    private Player player;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // Arrange
        player = new Player(1, "John Doe", LocalDate.now().toString(), 24, 2000.0, 2500.0, 500.0, 400.0, 300.0,"","","");
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

    @Test
    public void testGetAndSetCountry() {
        // Act
        player.setCountry("United States");

        // Assert
        assertEquals("United States", player.getCountry(), "Country should match the set value.");
    }

    @Test
    public void testGetAndSetBio() {
        // Act
        player.setBio("Professional player since 2020");

        // Assert
        assertEquals("Professional player since 2020", player.getBio(), "Bio should match the set value.");
    }

    @Test
    public void testGetAndSetAchievements() {
        // Act
        player.setAchievements("Winner of multiple tournaments");

        // Assert
        assertEquals("Winner of multiple tournaments", player.getAchievements(), "Achievements should match the set value.");
    }

    @Test
    public void testGetAndSetRank() {
        // Act
        player.setRank(10);

        // Assert
        assertEquals(10, player.getRank(), "Rank should match the set value.");
    }

    @Test
    public void testGetAndSetPeakAge() {
        // Act
        player.setPeakAge(25);

        // Assert
        assertEquals(25, player.getPeakAge(), "Peak age should match the set value.");
    }


    @Test
    public void testAge() {
        // Arrange
        LocalDate twentyFiveYearsAgo = LocalDate.now().minusYears(25);
        player.setDob(twentyFiveYearsAgo.toString());

        // Act
        Integer age = player.age();

        // Assert
        assertEquals(25, age, "Age calculation should be correct.");
    }

    @Test
    public void testAgeWithNullDob() {
        // Arrange
        player.setDob(null);

        // Act
        Integer age = player.age();

        // Assert
        assertNull(age, "Age should be null when DOB is null.");
    }

    @Test
    public void testGetAndSetPeakElo() {
        // Act
        player.setPeakElo(2800.0);

        // Assert
        assertEquals(2800.0, player.getPeakElo(), 0.0001, "Peak Elo should match the set value.");
    }

}