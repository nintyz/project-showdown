package com.projectshowdown.entities;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tournament {
    private String id;

    @NotNull(message = "Tournament name should not be empty")
    private String name;

    private int year;
    private String type;
    private String venue;
    private String date;
    private int numPlayers;
    private String status;

    private double minMMR;
    private double maxMMR;

    private ArrayList<User> users = new ArrayList<>();

    public Tournament(String id, String name, int year, String type, String venue, String date, int numPlayers, String status, double minMMR, double maxMMR) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.type = type;
        this.venue = venue;
        this.date = date;
        this.numPlayers = numPlayers;
        this.status = status;
        this.minMMR = minMMR;
        this.maxMMR = maxMMR;
        this.users = new ArrayList<>();
    }

    public String getTournamentId() {
        return id;
    }
    
    public void setTournamentId(String id) {
        this.id = id;
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
    
    public int getNumPlayers() {
        return numPlayers;
    }
    
    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
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
    
    public ArrayList<User> getUsers() {
        return users;
    }
    
    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public void addUser(User player) {
        double playerMMR = player.getPlayerDetails().calculateMMR();

        // Check if the user's MMR is within the tournament's range
        if (playerMMR >= minMMR && playerMMR <= maxMMR) {
            this.users.add(player);
        } else {
            throw new IllegalArgumentException("Player with MMR " + playerMMR + " is not eligible for this tournament (Allowed range: " + minMMR + " - " + maxMMR + ")");
        }
    }

    public TreeMap<Integer, User> getSeedings() {
        TreeMap<Integer, User> seedings = new TreeMap<>();
        ArrayList<User> users = this.getUsers(); 

        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User p1, User p2) {
                double mmr1 = p1.getPlayerDetails().calculateMMR();
                double mmr2 = p2.getPlayerDetails().calculateMMR();
                return Double.compare(mmr2, mmr1);
            }
        });

        for (int i = 0; i < users.size(); i++) {
            User player = users.get(i);
            seedings.put(i + 1, player);
        }

        return seedings;
    }

    public List<Match> createMatches() {
        List<Match> matches = new ArrayList<>();

        Collections.sort(users, Comparator.comparingDouble(user -> user.getPlayerDetails().calculateMMR()));

        if (users.size() % 2 != 0) {
            throw new IllegalStateException("The number of players should be even to create matches.");
        }
        
        int max = users.size() / 2;
        int increment = max / 2;

        for (int i = 0; i < users.size() / 2; i++) {
            User user1 = users.get(i);
            User user2 = users.get(users.size() - 1 - i);

            double mmrDifference = Math.abs(user1.getPlayerDetails().calculateMMR() - user2.getPlayerDetails().calculateMMR());

            // logic to get the match id and number
            int bracket = i % increment;
            int matchNumber = 0;
            
            if(i % 2 == 0){
                if (i < increment){
                    matchNumber = bracket + 1;
                }
                if (i >= increment){
                    matchNumber = bracket + 2;
                }
            } else {
                if (i <= increment){
                    matchNumber = max - bracket + 1;
                }
                if (i > increment){
                    matchNumber = max - bracket;
                }
            }

            Match match = new Match();
            match.setTournamentId(this.id);
            match.setPlayer1Id(user1.getId());
            match.setPlayer2Id(user2.getId());
            match.setPlayer1Score(0);
            match.setPlayer2Score(0);
            match.setMmrDifference(mmrDifference);
            match.setMatchDate(this.date);
            match.setStage("Round 1");
            String matchId = generateMatchId(match, matchNumber);
            match.setMatchId(matchId);
            matches.add(match);
        }

        return matches;
    }

    private String generateMatchId(Match m, int matchNo) {
        String number = "" + matchNo;
        return this.name + "_" + m.getStage() + "_" + number;
    }
}