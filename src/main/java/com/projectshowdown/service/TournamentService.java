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

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.projectshowdown.entities.Tournament;
import com.projectshowdown.exceptions.TournamentNotFoundException;

@Service
public class TournamentService {

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

        List<String> currentUsers = (List<String>) document.get("users");

        if (currentUsers == null) {
            currentUsers = new ArrayList<>(); // Initialize a new list if none exists
        } else {
            // check if user already registered.
            for (String user : currentUsers) {
                if (user.equals(userId)) {
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

}