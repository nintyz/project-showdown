package com.projectshowdown.entities;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.projectshowdown.dto.UserDTO;

/**
 * Represents a tournament in the application.
 * Contains details about the tournament, such as name, venue, participants,
 * and related rounds.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tournament {
    /**
     * The unique identifier for the tournament.
     */
    private String id;

    /**
     * The name of the tournament.
     */
    @NotNull(message = "Tournament name should not be empty")
    private String name;

    /**
     * The year the tournament is held.
     */
    private int year;

    /**
     * The venue where the tournament takes place.
     */
    private String venue;

    /**
     * The country where the tournament is held.
     */
    private String country;

    /**
     * The date and time of the tournament in ISO format (e.g., "YYYY-MM-DDTHH:mm:ss").
     */
    private String dateTime;

    /**
     * The total number of players in the tournament.
     */
    private int numPlayers;

    /**
     * The current status of the tournament (e.g., "In Progress", "Completed").
     */
    private String status;

    /**
     * The minimum MMR (Matchmaking Rating) required to join the tournament.
     */
    private double minMMR;

    /**
     * The maximum MMR (Matchmaking Rating) allowed to join the tournament.
     */
    private double maxMMR;

    /**
     * The URL of the tournament's logo.
     */
    private String logoUrl;

    /**
     * The rounds associated with the tournament.
     */
    private List<Round> rounds = new ArrayList<>(); 

    /**
     * The ID of the tournament's organizer.
     */
    private String organizerId;

    /**
     * A list of user IDs registered for the tournament.
     */
    private List<String> users = new ArrayList<>();

    /**
     * Retrieves the rounds of the tournament.
     * Initializes the rounds list if it is null.
     *
     * @return A list of rounds in the tournament.
     */
    public List<Round> getRounds() {
        if (this.rounds == null) {
            this.rounds = new ArrayList<>();
        }
        return this.rounds;
    }
    
    /**
     * Calculates the total number of matches across all rounds in the tournament.
     *
     * @return The total number of matches in the tournament.
     */
    public int totalMatches() {
        int counter = 0;
        for (int i = 0; i < rounds.size(); i++) {
            counter += rounds.get(i).getMatches().size();
        }
        return counter;
    }

    /**
     * Checks whether a user is eligible to participate in the tournament based on their MMR.
     *
     * @param player The player to check eligibility for.
     * @return {@code true} if the player's MMR is within the tournament's range; {@code false} otherwise.
     */
    public boolean checkUserEligibility(UserDTO player) {
        double playerMMR = player.getPlayerDetails().calculateMMR();

        // Check if the user's MMR is within the tournament's range
        if (playerMMR >= minMMR && playerMMR <= maxMMR) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks whether the tournament date is valid for a player to participate.
     *
     * @param player The player to check the date for.
     * @return {@code true} if the current date is before the tournament's date; {@code false} otherwise.
     */
    public boolean checkDate(UserDTO player) {
        return !LocalDateTime.now().isAfter(LocalDateTime.parse(dateTime));
    }

    public boolean inProgress(){
        return status.equalsIgnoreCase("in progress");
    }

}