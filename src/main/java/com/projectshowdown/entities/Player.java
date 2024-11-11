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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Player {

    private int rank;

    private String dob;

    @Positive(message = "The value must be a positive number")
    private double elo;

    private double peakAge;
    @Positive(message = "The value must be a positive number")
    private double peakElo;

    private String country;
    private String bio;
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

    // Method to calculate MMR based on the given formula
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
