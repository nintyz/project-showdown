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
    private String dateTime;
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


}