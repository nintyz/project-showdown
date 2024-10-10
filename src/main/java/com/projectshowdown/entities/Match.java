package com.projectshowdown.entities;

public class Match {

    private String matchId;
    private String tournamentId;
    private String player1Id;
    private String player2Id;
    private String winnerId;
    private String score;
    private double mmrDifference;
    private String matchDate;
    private String stage;

    // Default constructor is required by Firebase
    public Match() {
    }

    // Parameterized constructor
    public Match(String matchId, String tournamentId, String player1Id, String player2Id, String winnerId, String score,
                 double mmrDifference, String matchDate, String stage) {
        this.matchId = matchId;
        this.tournamentId = tournamentId;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.winnerId = winnerId;
        this.score = score;
        this.mmrDifference = mmrDifference;
        this.matchDate = matchDate;
        this.stage = stage;
    }

    // Getters and setters
    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(String tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(String player1Id) {
        this.player1Id = player1Id;
    }

    public String getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(String player2Id) {
        this.player2Id = player2Id;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public double getMmrDifference() {
        return mmrDifference;
    }

    public void setMmrDifference(double mmrDifference) {
        this.mmrDifference = mmrDifference;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }
}