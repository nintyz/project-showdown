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

    private String matchId;
    private String tournamentId;
    private String player1Id;
    private String player2Id;
    private String winnerId;
    private String score;
    private double mmrDifference;
    private String matchDate;
    private String stage;

}