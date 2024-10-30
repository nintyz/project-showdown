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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.entities.Match;
import com.projectshowdown.entities.Round;
import com.projectshowdown.entities.Tournament;
import com.projectshowdown.exceptions.TournamentNotFoundException;
import com.projectshowdown.entities.User;

@Service
public class TournamentService {

    @Autowired
    UserService userService;

    @Autowired
    MatchService matchService;

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

        // Perform the update operation
        ApiFuture<WriteResult> writeResult = docRef.update(filteredUpdates);

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

        UserDTO user = userService.getPlayer(userId);
        Tournament tournament = getTournament(tournamentId);
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

    public String progressTournament(String tournamentId) throws ExecutionException, InterruptedException {
        Tournament tournament = getTournament(tournamentId);
        switch (tournament.getRounds().size()) {
            case 0:
                return initializeTournament(tournament);
            case 1:
                return generateNextRound(tournament, "Quarter Finals", 0);
            case 2:
            case 3:
                return "Not Implemented yet";
            default:
                return "Error occurred";
        }
    }

    public String initializeTournament(Tournament tournament) {
        try {
            List<User> users = userService.getRegisteredUsers(tournament.getUsers());
            Collections.sort(users, Comparator.comparingDouble(user -> user.getPlayerDetails().calculateMMR()));
            
            if (users.size() != tournament.getNumPlayers()) {
                return "The required amount of registered players have not been met!";
            }

            List<String> matches = generateMatches(tournament, users, "Round 1");
            addRoundToTournament(tournament, "Initial", matches);

            return matches.size() + " matches have been generated for tournament id " + tournament.getId();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String generateNextRound(Tournament tournament, String roundName, int previousRoundIndex) {
        try {
            List<User> users = userService.getWinningUsers(tournament.getRounds().get(previousRoundIndex).getMatches());
            List<String> matches = generateMatches(tournament, users, roundName);

            addRoundToTournament(tournament, roundName, matches);

            return matches.size() + " matches have been generated for tournament id " + tournament.getId() + " for the "
                    + roundName;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private List<String> generateMatches(Tournament tournament, List<User> users, String stage)
            throws ExecutionException, InterruptedException {
        List<String> matches = new ArrayList<>();

        for (int i = 0; i < users.size() / 2; i++) {
            User user1 = users.get(i);
            User user2 = users.get(users.size() - 1 - i);

            Match match = createMatch(tournament, user1, user2, stage);
            matches.add(matchService.addMatch(match));
        }

        return matches;
    }

    private Match createMatch(Tournament tournament, User user1, User user2, String stage) {
        Match match = new Match();
        match.setTournamentId(tournament.getId());
        match.setPlayer1Id(user1.getId());
        match.setPlayer2Id(user2.getId());
        match.setPlayer1Score(0);
        match.setPlayer2Score(0);
        match.setMmrDifference(
                Math.abs(user1.getPlayerDetails().calculateMMR() - user2.getPlayerDetails().calculateMMR()));
        match.setMatchDate(tournament.getDate());
        match.setStage(stage);
        return match;
    }

    private void addRoundToTournament(Tournament tournament, String roundName, List<String> matches) {
        Round newRound = new Round(roundName, matches);
        tournament.getRounds().add(newRound);

        Map<String, Object> jsonBody = new HashMap<>();
        jsonBody.put("rounds", tournament.getRounds());
        try {
            updateTournament(tournament.getId(), jsonBody);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}