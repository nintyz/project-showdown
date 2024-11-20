package com.projectshowdown.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a match in a tournament.
 * Stores details about the participants, scores, and match status.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Match {

    /**
     * The unique identifier of the match.
     */
    private String id;

    /**
     * The ID of the tournament this match belongs to.
     */
    private String tournamentId;

    /**
     * The ID of the first player.
     */
    private String player1Id;

    /**
     * The ID of the second player.
     */
    private String player2Id;

    /**
     * The score of the first player for a match
     */
    private int player1Score;

    /**
     * The score of the second player for a match
     */
    private int player2Score;

    /**
     * The MMR (Matchmaking Rating) difference between the two players.
     */
    private double mmrDifference;

    /**
     * The date and time of the match.
     */
    private String dateTime;

    /**
     * The stage of the tournament (e.g., "Quarterfinals", "Semifinals").
     */
    private String stage;

    /**
     * Indicates whether the match is completed.
     */
    private boolean completed;

    /**
     * Determines the winner of the match based on scores.
     *
     * @return The ID of the winning player.
     */
    public String winnerId() {
        String winnerId = player1Id;
        if (player2Score > player1Score) {
            winnerId = player2Id;
        }
        return winnerId;
    }

    /**
     * Determines the loser of the match based on scores.
     *
     * @return The ID of the losing player.
     */
    public String loserId() {
        String loserId = player1Id;
        if (loserId.equals(winnerId())) {
            loserId = player2Id;
        }
        return loserId;
    }


}