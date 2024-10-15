package com.projectshowdown.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
    private Date date;

    @ExactPlayers(message = "The tournament must have exactly 32 players")
    private final ArrayList<Player> players = new ArrayList<Player>();

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    // implement the bracket from the players
    // arraylist of players participating
    public TreeMap<Integer, Player> getSeedings() {
        TreeMap<Integer, Player> seedings = new TreeMap<>();
        ArrayList<Player> players = this.getPlayers(); // Retrieve players

        // Sort players by MMR in descending order
        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                double mmr1 = p1.calculateMMR();
                double mmr2 = p2.calculateMMR();
                // Sort in descending order of MMR (highest MMR first)
                return Double.compare(mmr2, mmr1);
            }
        });

        // Assign seedings based on sorted order
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            seedings.put(i + 1, player); // i + 1 because seedings start from 1
        }

        return seedings; // Return the sorted seedings TreeMap
    }

    // method that creates matches based on seedings
    public List<Match> createMatches() {
        List<Match> matches = new ArrayList<>();

        // Sort players based on their MMR (or seedings)
        // assume that the number of players is already 32
        Collections.sort(players, Comparator.comparingDouble(Player::calculateMMR));
        // Create matches by pairing best vs. worst
        for (int i = 0; i < players.size() / 2; i++) {
            Player player1 = players.get(i); // Best seeded player
            Player player2 = players.get(players.size() - 1 - i); // Worst seeded player

            // Create match details
            String matchId = generateMatchId(); // Generate a unique match ID
            double mmrDifference = Math.abs(player1.calculateMMR() - player2.calculateMMR());

            Match match = new Match();
            match.setMatchId(matchId);
            match.setTournamentId(this.tournamentId);
            match.setPlayer1Id(player1.getId());
            match.setPlayer2Id(player2.getId());
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
        return this.name + System.currentTimeMillis();  // Simple example using timestamp
    }
}