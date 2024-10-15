package com.projectshowdown.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import com.projectshowdown.validation.ExactPlayers;

// import com.google.firebase.database.DatabaseReference;
// import com.google.firebase.database.FirebaseDatabase;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tournament {

    @NotNull(message = "Tournament ID should not be empty")
    private String tournamentId;

    @NotNull(message = "Tournament name should not be empty")
    private String name;

    private int year;
    private String type;
    private String venue;
    private String date;

    @ExactPlayers(message = "The tournament must have exactly 32 users")
    private ArrayList<User> users = new ArrayList<User>();

    // Parameterized constructor with essential fields
    public Tournament(String tournamentId, String name, int year, String type) {
        this.tournamentId = tournamentId;
        this.name = name;
        this.year = year;
        this.type = type;
        this.users = new ArrayList<>(); // Initialize with an empty list
    }

    // Getters and Setters
    public String getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(String tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void addUser(User player) {
        this.users.add(player);
    }

    // implement the bracket from the users
    // arraylist of users participating
    public TreeMap<Integer, User> getSeedings() {
        TreeMap<Integer, User> seedings = new TreeMap<>();
        ArrayList<User> users = this.getUsers(); // Retrieve users

        // Sort users by MMR in descending order
        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User p1, User p2) {
                double mmr1 = p1.getPlayerDetails().calculateMMR();
                double mmr2 = p2.getPlayerDetails().calculateMMR();
                // Sort in descending order of MMR (highest MMR first)
                return Double.compare(mmr2, mmr1);
            }
        });

        // Assign seedings based on sorted order
        for (int i = 0; i < users.size(); i++) {
            User player = users.get(i);
            seedings.put(i + 1, player); // i + 1 because seedings start from 1
        }

        return seedings; // Return the sorted seedings TreeMap
    }

    // method that creates matches based on seedings
    public List<Match> createMatches() {
        List<Match> matches = new ArrayList<>();

        // Sort users based on their MMR (or seedings)
        // assume that the number of users is already 32
        Collections.sort(users, Comparator.comparingDouble(user -> user.getPlayerDetails().calculateMMR()));
        // Create matches by pairing best vs. worst
        for (int i = 0; i < users.size() / 2; i++) {
            User user1 = users.get(i); // Best seeded player
            User user2 = users.get(users.size() - 1 - i); // Worst seeded player

            // Create match details
            String matchId = generateMatchId(); // Generate a unique match ID
            double mmrDifference = Math
                    .abs(user1.getPlayerDetails().calculateMMR() - user2.getPlayerDetails().calculateMMR());

            Match match = new Match();
            match.setMatchId(matchId);
            match.setTournamentId(this.tournamentId);
            match.setPlayer1Id(user1.getId());
            match.setPlayer2Id(user2.getId());
            match.setScore("0-0"); // Initial score
            match.setMmrDifference(mmrDifference);
            match.setMatchDate(""); // Set the current date
            match.setStage("Round 1"); // You can change this based on the tournament structure

            // Add the match to the list
            matches.add(match);
        }

        return matches;
    }

    private String generateMatchId() {
        // Implement unique ID generation logic here
        return this.name + System.currentTimeMillis(); // Simple example using timestamp
    }
}