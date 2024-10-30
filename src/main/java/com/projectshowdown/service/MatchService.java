package com.projectshowdown.service;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.projectshowdown.entities.Match;

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
}
