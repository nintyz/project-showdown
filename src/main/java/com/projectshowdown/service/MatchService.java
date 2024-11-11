package com.projectshowdown.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.dto.UserMapper;
import com.projectshowdown.entities.User;
import com.projectshowdown.entities.Match;
import com.projectshowdown.events.MatchUpdatedEvent;

import jakarta.mail.MessagingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class MatchService {
    @Autowired
    UserService userService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private final static int ELO_GAINED_WHEN_YOU_WIN = 25;

    // Helper method to get Firestore instance
    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    public String addMatch(Match matchToSave) throws ExecutionException, InterruptedException {
        Firestore db = getFirestore();
        // Generate a new document reference with a random ID
        DocumentReference docRef = db.collection("matches").document(matchToSave.getId());

        // Save the tournament to Firestore

        ApiFuture<WriteResult> writeResult = docRef.set(matchToSave);
        return matchToSave.getId();

    }

    // Method to update a tournament document in the 'tournaments' collection
    public String updateMatch(String id, Map<String, Object> matchData)
            throws ExecutionException, InterruptedException {
        Firestore db = getFirestore();

        matchData.put("completed", true);

        // Get a reference to the document
        DocumentReference docRef = db.collection("matches").document(id);

        // Check if the document exists
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (!document.exists()) {
            return "Unable to find match with id: " + id;
        }

        // Retrieve tournamentId from the match document for later use
        String tournamentId = document.getString("tournamentId");

        // if current match dateTime is TBC, and you're not trying to update the
        // dateTime, should prompt to update dateTime
        if (((String) document.get("dateTime")).equals("TBC") && !matchData.containsKey("dateTime")) {
            return "Please update match's date and time details before attempting to update the scores";
        }

        // if trying to update match date & time
        if (matchData.containsKey("dateTime")) {
            // Extract user IDs from match document
            String user1Id = document.getString("player1Id");
            String user2Id = document.getString("player2Id");

            String newDateTime = (String) matchData.get("dateTime");

            // Extract date and time from newDateTime (assuming format
            // "yyyy-MM-dd'T'HH:mm:ss:SS")
            String[] dateTimeParts = newDateTime.split("T");
            String date = dateTimeParts[0]; // e.g., "2024-12-31"
            String time = dateTimeParts[1].substring(0, 5); // Extract "HH:mm" part

            // Convert time to AM/PM format
            String amPmTime;
            try {
                java.time.LocalTime localTime = java.time.LocalTime.parse(time);
                amPmTime = localTime.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a")); // e.g., "02:15
                                                                                                      // PM"
            } catch (Exception e) {
                amPmTime = time; // fallback in case of parsing issue
            }

            // Retrieve tournament information using tournamentId from match document
            DocumentReference tournamentRef = db.collection("tournaments").document(tournamentId);
            DocumentSnapshot tournamentDocument = tournamentRef.get().get();
            String tournamentName = tournamentDocument.exists() ? tournamentDocument.getString("name")
                    : "Unknown Tournament";

            // Retrieve user information
            UserDTO user1 = userService.getUser(user1Id);
            UserDTO user2 = userService.getUser(user2Id);

            // Send emails to both players
            try {
                System.out.println("Sending email notification for updated match timing...");

                notificationService.notifyMatchDetailsUpdated(
                        user1.getEmail(),
                        user1.getPlayerDetails().getName(),
                        user2.getPlayerDetails().getName(),
                        tournamentName,
                        date,
                        amPmTime);

                notificationService.notifyMatchDetailsUpdated(
                        user2.getEmail(),
                        user2.getPlayerDetails().getName(),
                        user1.getPlayerDetails().getName(),
                        tournamentName,
                        date,
                        amPmTime);
            } catch (MessagingException e) {
                System.out.println("Failed to send updated match notification emails.");
                e.printStackTrace();
            }

        }

        // Filter out null values from the update data
        Map<String, Object> filteredUpdates = matchData.entrySet().stream()
                .filter(entry -> entry.getValue() != null) // Only include non-null fields
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Perform the update operation
        ApiFuture<WriteResult> writeResult = docRef.update(filteredUpdates);

        // check if Round has been completed
        // Publish event
        Match match = document.toObject(Match.class);
        eventPublisher.publishEvent(new MatchUpdatedEvent(this, tournamentId, match));

        // if trying to update scores, update the winner player's elo
        if (matchData.containsKey("player1Score") && matchData.containsKey("player2Score")) {

            UserDTO winner = userService.getUser(match.winnerId());
            Map<String, Object> toUpdateScore = new HashMap<>();
            toUpdateScore.put("playerDetails.elo", winner.getPlayerDetails().getElo() + ELO_GAINED_WHEN_YOU_WIN);
            userService.updateUser(winner.getId(), toUpdateScore);
        }

        // Return success message with the update time
        return "Match with ID: " + id + " updated successfully at: " + writeResult.get().getUpdateTime();
    }

    public boolean checkCurrentRoundCompletion(List<String> currentRound)
            throws ExecutionException, InterruptedException {
        for (Match match : getMatches(currentRound)) {
            if (!match.isCompleted()) {
                return false;
            }
        }
        return true;
    }

    public Match getMatch(String matchId) throws ExecutionException, InterruptedException {
        Firestore db = getFirestore();
        // Generate a new document reference with a random ID
        DocumentReference documentReference = db.collection("matches").document(matchId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        // If the document exists, convert it to a Player object
        if (document.exists()) {
            Match matchToReturn = document.toObject(Match.class);
            matchToReturn.setId(matchId);
            return matchToReturn;
        } else {
            // Document doesn't exist, return null or handle it based on your needs
            throw new RuntimeException("unable to find match: " + matchId);
        }

    }

    public List<Match> getMatches(List<String> matchIds) throws ExecutionException, InterruptedException {
        List<Match> response = new ArrayList<>();
        for (String matchId : matchIds) {
            response.add(getMatch(matchId));
        }
        return response;
    }
}
