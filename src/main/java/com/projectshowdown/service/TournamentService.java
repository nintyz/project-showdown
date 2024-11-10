package com.projectshowdown.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.dto.UserMapper;
import com.projectshowdown.entities.Match;
import com.projectshowdown.entities.Round;
import com.projectshowdown.entities.Tournament;
import com.projectshowdown.exceptions.TournamentNotFoundException;

import jakarta.mail.MessagingException;

import com.projectshowdown.entities.User;
import com.projectshowdown.events.MatchUpdatedEvent;

@Service
public class TournamentService {

    @Autowired
    UserService userService;

    @Autowired
    MatchService matchService;

    @Autowired
    NotificationService notificationService;

    // Helper method to get Firestore instance
    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    public List<Tournament> getAllTournaments() throws ExecutionException, InterruptedException {
        Firestore db = getFirestore();
        Query tournamentsCollection = db.collection("tournaments");
        ApiFuture<QuerySnapshot> future = tournamentsCollection.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        // Prepare a list to hold each document's data
        List<Tournament> allTournaments = new ArrayList<>();

        // Iterate through the documents and add their data to the list
        for (DocumentSnapshot document : documents) {
            if (document.exists()) {
                // Add document data to the listt
                Tournament currTournament = document.toObject(Tournament.class);
                // set id.
                currTournament.setId(document.getId());
                allTournaments.add(currTournament);

            }
        }

        return allTournaments; // Return the list of tournament
    }

    // Method to save tournament details to Firestore
    public String addTournament(Tournament tournament) throws ExecutionException, InterruptedException {
        Firestore db = getFirestore();
        // Generate a new document reference with a random ID
        DocumentReference docRef = db.collection("tournaments").document();
        // Get the generated document ID
        String generatedId = docRef.getId();
        tournament.setId(generatedId);

        // Save the tournament to Firestore
        ApiFuture<WriteResult> writeResult = docRef.set(tournament);

        // Return success message with timestamp
        return "Tournament created successfully with ID: " + generatedId + " at: " + writeResult.get().getUpdateTime();
    }

    // Method to get specific tournament from firebase.
    public Tournament getTournament(String tournamentId) throws ExecutionException, InterruptedException {
        Firestore db = getFirestore();
        DocumentReference documentReference = db.collection("tournaments").document(tournamentId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();

        // If the document exists, convert it to a Player object
        if (document.exists()) {
            // You can directly map the Firestore data to a Player class
            Tournament tournamentToReturn = document.toObject(Tournament.class);
            tournamentToReturn.setId(tournamentId);
            return tournamentToReturn;
        } else {
            // Document doesn't exist, return null or handle it based on your needs
            throw new TournamentNotFoundException(tournamentId);
        }
    }

    // Method to get specific tournament from firebase.
    public Map<String, Object> displayTournament(String tournamentId) throws ExecutionException, InterruptedException {
        Firestore db = getFirestore();
        Map<String, Object> response = new HashMap<>();

        // Step 1: Fetch Tournament
        DocumentReference tournamentRef = db.collection("tournaments").document(tournamentId);
        DocumentSnapshot tournamentSnapshot = tournamentRef.get().get();
        if (!tournamentSnapshot.exists()) {
            throw new IllegalArgumentException("Tournament not found");
        }
        response.putAll(tournamentSnapshot.getData());

        List<Map<String, Object>> roundsData = new ArrayList<>();

        // Step 2: Iterate over each round
        List<Map<String, Object>> rounds = (List<Map<String, Object>>) tournamentSnapshot.get("rounds");
        for (Map<String, Object> round : rounds) {
            Map<String, Object> roundData = new HashMap<>();
            roundData.put("name", round.get("name"));

            List<String> matchIds = (List<String>) round.get("matches");
            List<Map<String, Object>> matchesData = new ArrayList<>();

            // Step 3: Fetch each match in the round
            for (String matchId : matchIds) {
                DocumentReference matchRef = db.collection("matches").document(matchId);
                DocumentSnapshot matchSnapshot = matchRef.get().get();
                if (matchSnapshot.exists()) {
                    Map<String, Object> matchData = matchSnapshot.getData();
                    String player1Id = (String) matchData.get("player1Id");
                    String player2Id = (String) matchData.get("player2Id");

                    // Fetch player1 and player2 data
                    DocumentReference player1Ref = db.collection("users").document(player1Id);
                    DocumentReference player2Ref = db.collection("users").document(player2Id);

                    DocumentSnapshot player1Snapshot = player1Ref.get().get();
                    DocumentSnapshot player2Snapshot = player2Ref.get().get();

                    if (player1Snapshot.exists() && player2Snapshot.exists()) {
                        matchData.put("player1", player1Snapshot.getData());
                        matchData.put("player2", player2Snapshot.getData());
                    }
                    matchesData.add(matchData);
                }
            }
            roundData.put("matches", matchesData);
            roundsData.add(roundData);
        }

        // Step 4: Add rounds data to tournament data
        response.put("rounds", roundsData);

        return response;
    }

    // Method to update a tournament document in the 'tournaments' collection
    public String updateTournament(String tournamentId, Map<String, Object> tournamentData)
            throws ExecutionException, InterruptedException {
        Firestore db = getFirestore();

        // Get a reference to the document
        DocumentReference docRef = db.collection("tournaments").document(tournamentId);

        // Check if the document exists
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (!document.exists()) {
            throw new TournamentNotFoundException(tournamentId);
        }

        // Filter out null values from the update data
        Map<String, Object> filteredUpdates = tournamentData.entrySet().stream()
                .filter(entry -> entry.getValue() != null) // Only include non-null fields
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // check if the update is to cancel tournament
        if (tournamentData.containsKey("status") && ((String) tournamentData.get("status")).equalsIgnoreCase("Cancelled")) {
            // check if tournament has begun
            if (((ArrayList<String>) document.get("rounds")).size() != 0) {
                return "You are not allowed to cancel a tournament that has already begun!";
            }
            // EMAIL NOTIFICATION TO LET REGISTERED PLAYERS KNOW ABOUT ITS CANCELLATION
            
            // Retrieve the tournament name from the document
            String tournamentName = document.getString("name");

            // Retrieve the list of registered users
            List<String> registeredUsers = (List<String>) document.get("users");
            for (String userId : registeredUsers) {
                UserDTO user = userService.getUser(userId);
                try {
                    // Send cancellation notification to each user
                    notificationService.notifyTournamentCancelled(user.getEmail(), tournamentName);
                } catch (MessagingException e) {
                    System.out.println("Failed to send cancellation notification to user: " + userId);
                    e.printStackTrace();
                }
            }
        }

        // Perform the update operation
        ApiFuture<WriteResult> writeResult = docRef.update(filteredUpdates);

        if(tournamentData.get("status").equals("cancelled")){
            // EMAIL NOTIFICATION TO LET REGISTERED PLAYERS KNOW ABOUT ITS CANCELLATION
        }

        // Return success message with the update time
        return "Tournament with ID: " + tournamentId + " updated successfully at: " + writeResult.get().getUpdateTime();
    }

    // Method to register a new player
    public String registerUser(String tournamentId, String userId)
            throws ExecutionException, InterruptedException {
        Firestore db = getFirestore();

        // Get a reference to the document
        DocumentReference docRef = db.collection("tournaments").document(tournamentId);

        // Check if the document exists
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (!document.exists()) {
            throw new TournamentNotFoundException(tournamentId);
        }

        // fetch data
        UserDTO user = userService.getUser(userId);
        Tournament tournament = getTournament(tournamentId);

        // check tournament started
        if (tournament.getRounds().size() != 0) {
            return "Tournament's has already begin";
        }

        // check registration date over
        if (!tournament.checkDate(user)) {
            return "Tournament's registration date is already over!";
        }

        // check mmr
        if (!tournament.checkUserEligibility(user)) {
            return "Player with MMR " + user.getPlayerDetails().calculateMMR()
                    + " is not eligible for this tournament (Allowed range: " + tournament.getMinMMR() + " - "
                    + tournament.getMaxMMR() + ")";
        }

        System.out.println("player MMR:" + user.getPlayerDetails().calculateMMR());

        List<String> currentUsers = (List<String>) document.get("users");

        if (currentUsers == null) {
            currentUsers = new ArrayList<>(); // Initialize a new list if none exists
        } else {
            // check if user already registered.
            for (String specificUser : currentUsers) {
                if (specificUser.equals(userId)) {
                    return "You have already registered for this Tournament!";
                }
            }
        }

        // Add the new player to the current list
        currentUsers.add(userId);

        // Update the 'players' field with the updated list
        ApiFuture<WriteResult> writeResult = docRef.update("users", currentUsers);

        // Return success message with the update time
        return "UserId:" + userId + " has successfully joined Tournament with ID: " + tournamentId
                + " successfully at: " + writeResult.get().getUpdateTime();
    }

    // Method to register a new player
    public String cancelRegistration(String tournamentId, String userId)
            throws ExecutionException, InterruptedException {
        Firestore db = getFirestore();

        // Get a reference to the document
        DocumentReference docRef = db.collection("tournaments").document(tournamentId);

        // Check if the document exists
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (!document.exists()) {
            throw new TournamentNotFoundException(tournamentId);
        }

        List<String> registeredUsers = (List<String>) document.get("users");

        if (registeredUsers.contains(userId)) {
            registeredUsers.remove(userId);
        } else {
            return "You are not registered to this event!";
        }

        // Update the 'players' field with the updated list
        ApiFuture<WriteResult> writeResult = docRef.update("users", registeredUsers);

        // Return success message with the update time
        return "UserId:" + userId + " has successfully unregistered from tournament: " + tournamentId
                + " at: " + writeResult.get().getUpdateTime();
    }

    @EventListener
    public void handleMatchUpdated(MatchUpdatedEvent event) throws ExecutionException, InterruptedException {
        String tournamentId = event.getTournamentId();
        // Check if tournament needs an update based on the match update
        Tournament tournament = getTournament(tournamentId);
        List<String> lastRound = tournament.getRounds().get(tournament.getRounds().size() - 1).getMatches();
        // already the finals, dont need update, so return
        // but need to update player achivements
        if (lastRound.size() == 1) {
            updateUserAchievements(tournament, event.getMatch().winnerId(), true);
            updateUserAchievements(tournament, event.getMatch().loserId(), false);
            return;
        }
        if (matchService.checkCurrentRoundCompletion(lastRound)) {
            progressTournament(tournamentId);
        }
    }

    public void updateUserAchievements(Tournament tournament, String userId, boolean gold)
            throws ExecutionException, InterruptedException {
        UserDTO user = userService.getUser(userId);
        HashMap<String, Object> achievements = new HashMap<>();
        String newAchievement = " Obtained " + (gold ? "Gold" : "Silver") + " from " + tournament.getName();
        achievements.put("playerDetails.achievements", user.getPlayerDetails().getAchievements() + newAchievement);

        userService.updateUser(userId, achievements);
    }

    public List<User> getWinningUsers(List<String> matches) throws ExecutionException, InterruptedException {
        List<User> response = new ArrayList<>();

        for (Match match : matchService.getMatches(matches)) {
            if (!match.isCompleted()) {
                throw new RuntimeException("Matches from the last round are not completed!");
            }
            response.add(UserMapper.toUser(userService.getUser(match.winnerId())));
        }

        return response;
    }

    public String progressTournament(String tournamentId) throws ExecutionException, InterruptedException {
        Tournament tournament = getTournament(tournamentId);
        String roundName = determineNextRoundName(tournament);

        if ("Error".equals(roundName)) {
            return "The tournament has already completed!";
        } else if ("Round 1".equals(roundName)) {
            return initializeTournament(tournament, roundName);
        }

        return generateNextRound(tournament, roundName);
    }

    private String determineNextRoundName(Tournament tournament) {
        int roundCount = tournament.getRounds().size();
        int totalUsers = tournament.getUsers().size();

        switch (roundCount) {
            case 0:
                return "Round 1";
            case 1:
                return totalUsers == 32 ? "Round 2" : "QuarterFinals";
            case 2:
                return totalUsers == 32 ? "QuarterFinals" : "Semi Finals";
            case 3:
                return totalUsers == 32 ? "Semi Finals" : "Finals";
            case 4:
                return totalUsers == 32 ? "Finals" : "Error";
            default:
                return "Error";
        }
    }

    public String initializeTournament(Tournament tournament, String roundName) {
        try {
            List<User> users = userService.getRegisteredUsers(tournament.getUsers());
            users.sort(Comparator.comparingDouble(user -> user.getPlayerDetails().calculateMMR()));

            if (users.size() != tournament.getNumPlayers()) {
                return "The required amount of registered players have not been met!";
            }

            List<String> matches = generateMatchesWithSeed(tournament, users, roundName, 0);
            addRoundToTournament(tournament, "Initial", matches);

            return matches.size() + " matches have been generated for tournament id " + tournament.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public String generateNextRound(Tournament tournament, String roundName) {
        try {
            List<String> lastRound = getLastRoundMatches(tournament);
            List<User> winners = getWinningUsers(lastRound);
            List<String> matches = generateFollowUpMatches(tournament, winners, roundName, tournament.totalMatches());

            addRoundToTournament(tournament, roundName, matches);

            return matches.size() + " matches have been generated for tournament id " + tournament.getId() + " for the "
                    + roundName;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private List<String> getLastRoundMatches(Tournament tournament) {
        List<String> lastRound = tournament.getRounds().get(tournament.getRounds().size() - 1).getMatches();
        lastRound.sort(Comparator.comparingInt(id -> Integer.parseInt(id.split("_")[1])));
        return lastRound;
    }

    private List<String> generateMatchesWithSeed(Tournament tournament, List<User> users, String stage,
            int totalMatches)
            throws ExecutionException, InterruptedException {
        List<String> matches = new ArrayList<>();
        List<Match> tempMatches = new ArrayList<>();
        Map<Integer, Integer> seedPositions = Map.of(
                1, 0,
                users.size() / 2, 1,
                users.size() / 4, 2,
                users.size() / 4 + 1, 3);

        int left = 4;
        int right = users.size() - 1;
        List<Integer> usedPlayers = new ArrayList<>();

        for (int i = 1; i <= users.size() / 2; i++) {
            User user1, user2;

            if (seedPositions.containsKey(i)) {
                int seedIndex = seedPositions.get(i);
                user1 = users.get(seedIndex);
                user2 = users.get(right);
                usedPlayers.add(seedIndex);
                usedPlayers.add(right--);
            } else {
                // Find the next available players for non-seeded matches
                while (usedPlayers.contains(left))
                    left++;

                user1 = users.get(left);
                usedPlayers.add(left++);

                while (usedPlayers.contains(right))
                    right--;

                user2 = users.get(right);
                usedPlayers.add(right--);
            }

            matches.add(createMatch(tournament, stage, user1, user2, totalMatches + matches.size() + 1));
            tempMatches.add(new Match("", tournament.getId(), user1.getId(), user2.getId(), 0, 0,
                    Math.abs(user1.getPlayerDetails().calculateMMR() - user2.getPlayerDetails().calculateMMR()),
                    "TBC", stage, false));

            // HERE to send emails to user1 and user2 of their matching with dateTime as TBC
        }
        return matches;
    }

    private String createMatch(Tournament tournament, String stage, User user1, User user2, int matchIndex)
            throws ExecutionException, InterruptedException {
        String matchId = tournament.getId() + "m_" + matchIndex;
        Match match = new Match(matchId, tournament.getId(), user1.getId(), user2.getId(), 0, 0,
                Math.abs(user1.getPlayerDetails().calculateMMR() - user2.getPlayerDetails().calculateMMR()),
                "TBC", stage, false);
        return matchService.addMatch(match);
    }

    private List<String> generateFollowUpMatches(Tournament tournament, List<User> users, String stage,
            int totalMatches)
            throws ExecutionException, InterruptedException {
        List<String> matches = new ArrayList<>();
        for (int i = 0; i < users.size(); i += 2) {
            matches.add(
                    createMatch(tournament, stage, users.get(i), users.get(i + 1), totalMatches + matches.size() + 1));
        }
        return matches;
    }

    private void addRoundToTournament(Tournament tournament, String roundName, List<String> matches)
            throws ExecutionException, InterruptedException {
        Round newRound = new Round(roundName, matches);
        tournament.getRounds().add(newRound);
        updateTournament(tournament.getId(), Map.of("rounds", tournament.getRounds()));
    }

}