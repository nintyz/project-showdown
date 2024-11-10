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
import com.projectshowdown.entities.Match;
import com.projectshowdown.events.MatchUpdatedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class MatchService {
    @Autowired
    private ApplicationEventPublisher eventPublisher;

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

        // if current match dateTime is TBC, and you're not trying to update the
        // dateTime, should prompt to update dateTime
        if (((String) document.get("dateTime")).equals("TBC") || matchData.containsKey("dateTime")) {
            return "Please update match's date and time details before attempting to update the scores";
        }

        if(matchData.containsKey("dateTime")){
            //here to inform players of their match timing.
            String user1Id = (String) document.get("player1Id");
            String user2Id = (String) document.get("player2Id");
        }
        String tournamentId = (String) document.get("tournamentId");

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
