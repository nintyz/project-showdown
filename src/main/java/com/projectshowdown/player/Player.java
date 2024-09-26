package com.projectshowdown.player;

import jakarta.validation.constraints.NotNull;

public class Player {
    private int id;
    private int rank;

    @NotNull(message = "Player's name should not be empty")
    private String name;

    private int age;
    private int peakAge;
    private double elo;
    private double peakElo;
    private double hardRaw;
    private double clayRaw;
    private double grassRaw;

    public Player() {
    }

    public Player(String name, int age, int peakAge, double elo, double peakElo, double hardRaw, double clayRaw,
            double grassRaw) {
        this.id = 0;
        this.rank = Integer.MAX_VALUE;
        this.name = name;
        this.age = age;
        this.elo = elo;
        this.peakElo = peakElo;
        this.peakAge = peakAge;
        this.hardRaw = hardRaw;
        this.clayRaw = clayRaw;
        this.grassRaw = grassRaw;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getId() {
        return id;
    }

    public long getRank() {
        return rank;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getPeakAge() {
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
}