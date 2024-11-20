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
    
    public void setRounds(List<Round> rounds) {
        this.rounds = rounds;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    /**
     * Checks whether the tournament is currently in progress.
     *
     * @return {@code true} if the tournament's status is "in progress"; {@code false} otherwise.
     */
    public boolean inProgress(){
        return status.equalsIgnoreCase("in progress");
    }

    public double getMinMMR() {
        return minMMR;
    }

    public void setMinMMR(double minMMR) {
        this.minMMR = minMMR;
    }

    public double getMaxMMR() {
        return maxMMR;
    }

    public void setMaxMMR(double maxMMR) {
        this.maxMMR = maxMMR;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
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
        if (LocalDateTime.now().isAfter(LocalDateTime.parse(dateTime))) {
            return false;
        }
        return true;
    }

}