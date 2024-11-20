package com.projectshowdown.entities;

import java.time.LocalDate;
import java.time.Period;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a player in the application.
 * Stores details about the player's rank, performance metrics, and achievements.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Player {

    /**
     * The player's rank in a tournament.
     */
    private int rank;

    /**
     * The player's date of birth in ISO format (e.g., "YYYY-MM-DD").
     */
    private String dob;

    /**
     * The player's current Elo rating, which must be a positive value.
     */
    @Positive(message = "The value must be a positive number")
    private double elo;

    /**
     * The player's age when they achieved their peak performance.
     */
    private double peakAge;

    /**
     * The player's peak Elo rating, which must be a positive value.
     */
    @Positive(message = "The value must be a positive number")
    private double peakElo;

    /**
     * The country the player represents.
     */
    private String country;

    /**
     * A brief biography of the player.
     */
    private String bio;

    /**
     * The player's achievements or notable milestones.
     */
    private String achievements;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    /**
     * Calculates the player's current age based on their date of birth.
     *
     * @return The player's age in years, or {@code null} if the date of birth is not set.
     */
    public Integer age() {
        if (dob == null) {
            return null;
        }
        LocalDate actualDob = LocalDate.parse(dob);
        return Period.between(actualDob, LocalDate.now()).getYears();
    }

    public double getPeakAge() {
        return peakAge;
    }

    public void setPeakAge(double peakAge) {
        this.peakAge = peakAge;
    }

    public double getElo() {
        return elo;
    }

    public void setElo(double elo) {
        this.elo = elo;
    }

    public double getPeakElo() {
        return peakElo;
    }

    public void setPeakElo(double peakElo) {
        this.peakElo = peakElo;
    }

    /**
     * Calculates the Matchmaking Rating (MMR) for the player.
     * 
     * The formula considers the player's current Elo, peak Elo, and age relative
     * to their peak performance age.
     *
     * @return The calculated MMR value.
     */
    public double calculateMMR() {

        double currentElo = this.getElo();
        double peakElo = this.getPeakElo();
        double age = this.age();
        double peakAge = this.getPeakAge();
        double yearsSincePeak = age - peakAge;

        // Pi constant
        final double PI = Math.PI;

        // Calculate the cosine part of the formula
        double cosValue = Math.cos((yearsSincePeak * PI) / 10);

        // Apply the formula
        return currentElo + peakElo * cosValue + 1000;
    }
}
