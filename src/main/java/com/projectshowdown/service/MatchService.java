package com.projectshowdown.service;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.projectshowdown.entities.Match;
import com.projectshowdown.entities.Tournament;
import com.projectshowdown.exceptions.TournamentNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class MatchService {

    // Helper method to get Firestore instance
    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    public String addMatch(Match matchToSave) throws ExecutionException, InterruptedException {
        Firestore db = getFirestore();
        // Generate a new document reference with a random ID
        DocumentReference docRef = db.collection("matches").document();
        // Get the generated document ID
        String generatedId = docRef.getId();
        matchToSave.setId(generatedId);

        // Save the tournament to Firestore

        ApiFuture<WriteResult> writeResult = docRef.set(matchToSave);
        return generatedId;

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
