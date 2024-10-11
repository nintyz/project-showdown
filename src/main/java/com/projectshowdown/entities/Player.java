package com.projectshowdown.entities;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class Player{

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
    }

    public Player(String name, int rank, double age, double peakAge, double elo, double peakElo, double hardRaw,
            double clayRaw,
            double grassRaw) {
        this.name = name;
        this.age = age;
        this.elo = elo;
        this.peakElo = peakElo;
        this.peakAge = peakAge;
        this.hardRaw = hardRaw;
        this.clayRaw = clayRaw;
        this.grassRaw = grassRaw;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setElo(double elo) {
        this.elo = elo;
    }

    public void setPeakElo(double peakElo) {
        this.peakElo = peakElo;
    }

    public void setPeakAge(int peakAge) {
        this.peakAge = peakAge;
    }

    public void setHardRaw(double hardRaw) {
        this.hardRaw = hardRaw;
    }

    public void setClayRaw(double clayRaw) {
        this.clayRaw = clayRaw;
    }

    public void setGrassRaw(double grassRaw) {
        this.grassRaw = grassRaw;
    }

    public long getRank() {
        return rank;
    }

    public String getName() {
        return name;
    }

    public double getAge() {
        return age;
    }

    public double getPeakAge() {
        return peakAge;
    }

    public double getElo() {
        return elo;
    }

    public double getPeakElo() {
        return peakElo;
    }

    public double getHardRaw() {
        return hardRaw;
    }

    public double getClayRaw() {
        return clayRaw;
    }

    public double getGrassRaw() {
        return grassRaw;
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
