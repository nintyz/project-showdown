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

    public Player() {
        // Initialize fields if necessary
    }

    // Parameterized constructor with essential fields
    public Player(String id, int rank, String name, int age) {
        this.id = id;
        this.rank = rank;
        this.name = name;
        this.age = age;
    }

    // Full parameterized constructor with all fields
    public Player(String id, int rank, String name, double age, double peakAge, double elo, double peakElo, 
                  double hardRaw, double clayRaw, double grassRaw) {
        this.id = id;
        this.rank = rank;
        this.name = name;
        this.age = age;
        this.peakAge = peakAge;
        this.elo = elo;
        this.peakElo = peakElo;
        this.hardRaw = hardRaw;
        this.clayRaw = clayRaw;
        this.grassRaw = grassRaw;
    }

     // Getters and Setters

     public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAge() {
        return age;
    }

    public void setAge(double age) {
        this.age = age;
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

    public double getHardRaw() {
        return hardRaw;
    }

    public void setHardRaw(double hardRaw) {
        this.hardRaw = hardRaw;
    }

    public double getClayRaw() {
        return clayRaw;
    }

    public void setClayRaw(double clayRaw) {
        this.clayRaw = clayRaw;
    }

    public double getGrassRaw() {
        return grassRaw;
    }

    public void setGrassRaw(double grassRaw) {
        this.grassRaw = grassRaw;
    }

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
