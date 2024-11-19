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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tournament {
    private String id;

    @NotNull(message = "Tournament name should not be empty")
    private String name;
    private int year;
    private String venue;
    private String country;
    private String dateTime;
    private int numPlayers;
    private String status;
    private double minMMR;
    private double maxMMR;
    private String logoUrl;
    private List<Round> rounds = new ArrayList<>(); 

    private String organizerId;
    private List<String> users = new ArrayList<>();

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

    public int totalMatches() {
        int counter = 0;
        for (int i = 0; i < rounds.size(); i++) {
            counter += rounds.get(i).getMatches().size();
        }
        return counter;
    }

    public boolean checkUserEligibility(UserDTO player) {
        double playerMMR = player.getPlayerDetails().calculateMMR();

        // Check if the user's MMR is within the tournament's range
        if (playerMMR >= minMMR && playerMMR <= maxMMR) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkDate(UserDTO player) {
        if (LocalDateTime.now().isAfter(LocalDateTime.parse(dateTime))) {
            return false;
        }
        return true;
    }

}