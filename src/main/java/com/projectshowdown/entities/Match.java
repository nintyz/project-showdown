package com.projectshowdown.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Match {

    private String id;
    private String tournamentId;
    private String player1Id;
    private String player2Id;
    private int player1Score;
    private int player2Score;
    private double mmrDifference;
    private String date;
    private String stage;
    private boolean completed;

    public String winnerId() {
        String winnerId = player1Id;
        if (player2Score > player1Score) {
            winnerId = player2Id;
        }
        return winnerId;
    }

    public String loserId() {
        String loserId = player1Id;
        if (loserId.equals(winnerId())) {
            loserId = player2Id;
        }
        return loserId;
    }

    // Getter for id
    public String getId() {
        return id;
    }

    // Setter for id
    public void setId(String id) {
        this.id = id;
    }

    // Getter for tournamentId
    public String getTournamentId() {
        return tournamentId;
    }

    // Setter for tournamentId
    public void setTournamentId(String tournamentId) {
        this.tournamentId = tournamentId;
    }

    // Getter for player1Id
    public String getPlayer1Id() {
        return player1Id;
    }

    // Setter for player1Id
    public void setPlayer1Id(String player1Id) {
        this.player1Id = player1Id;
    }

    // Getter for player2Id
    public String getPlayer2Id() {
        return player2Id;
    }

    // Setter for player2Id
    public void setPlayer2Id(String player2Id) {
        this.player2Id = player2Id;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public void setPlayer1Score(int score) {
        this.player1Score = score;
    }

    public int getPlayer2Score() {
        return this.player2Score;
    }

    public void setPlayer2Score(int score) {
        this.player2Score = score;
    }

    // Getter for mmrDifference
    public double getMmrDifference() {
        return mmrDifference;
    }

    // Setter for mmrDifference
    public void setMmrDifference(double mmrDifference) {
        this.mmrDifference = mmrDifference;
    }

    // Getter for matchDate
    public String getDate() {
        return date;
    }

    // Setter for matchDate
    public void setMatchDate(String date) {
        this.date = date;
    }

    // Getter for stage
    public String getStage() {
        return stage;
    }

    // Setter for stage
    public void setStage(String stage) {
        this.stage = stage;
    }

}