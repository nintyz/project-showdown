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
        return !LocalDateTime.now().isAfter(LocalDateTime.parse(dateTime));
    }

}