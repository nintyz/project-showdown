package com.projectshowdown.entities;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class Player{

    private String id;

    private int rank;

    @NotNull(message = "Name is mandatory")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Min(value = 0, message = "Age must be a positive number")
    @Max(value = 120, message = "Age must be less than or equal to 120")
    private double age;

    private double peakAge;

    @Positive(message = "The value must be a positive number")
    private double elo;

    @Positive(message = "The value must be a positive number")
    private double peakElo;

    @Positive(message = "The value must be a positive number")
    private double hardRaw;

    @Positive(message = "The value must be a positive number")
    private double clayRaw;

    @Positive(message = "The value must be a positive number")
    private double grassRaw;

    // Method to calculate MMR based on the given formula
    public double calculateMMR() {

        double currentElo = this.getElo();
        double peakElo = this.getPeakElo();
        double age = this.getAge();
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
