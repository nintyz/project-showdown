// package com.projectshowdown.service;

// import com.google.api.core.ApiFuture;
// import com.google.cloud.firestore.DocumentReference;
// import com.google.cloud.firestore.DocumentSnapshot;
// import com.google.cloud.firestore.Firestore;
// import com.google.cloud.firestore.Query;
// import com.google.cloud.firestore.QueryDocumentSnapshot;
// import com.google.cloud.firestore.QuerySnapshot;
// import com.google.cloud.firestore.WriteResult;
// import com.google.firebase.cloud.FirestoreClient;
// import com.projectshowdown.user.Match;
// import com.projectshowdown.user.Tournament;

// import org.springframework.stereotype.Service;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.Map;
// import java.util.concurrent.ExecutionException;

// @Service
// public class CustomTournamentDetailsService {

//     private final Firestore db;

//     public CustomTournamentDetailsService() {
//         db = FirestoreClient.getFirestore();
//     }

//     // Method to save tournament details to Firestore
//     public String saveTournament(Tournament tournament) throws ExecutionException, InterruptedException {
//         // Generate a unique tournament ID if not present
//         if (tournament.getTournamentId() == null || tournament.getTournamentId().isEmpty()) {
//             tournament.setTournamentId(db.collection("tournaments").document().getId()); // Generate a unique ID
//         }

//         DocumentReference docRef = db.collection("tournaments").document(tournament.getTournamentId());
//         ApiFuture<WriteResult> writeResult = docRef.set(tournament);
//         return "Tournament created successfully at: " + writeResult.get().getUpdateTime();
//     }

//     // Method to retrieve tournament details from Firestore by ID
//     public Tournament getTournamentById(String tournamentId) throws ExecutionException, InterruptedException {
//         DocumentReference docRef = db.collection("tournaments").document(tournamentId);
//         ApiFuture<DocumentSnapshot> future = docRef.get();
//         DocumentSnapshot document = future.get();

//         if (document.exists()) {
//             return document.toObject(Tournament.class);
//         } else {
//             throw new IllegalArgumentException("Tournament not found: " + tournamentId);
//         }
//     }

//     // Method to retrieve all matches in a tournament
//     public List<Match> getMatchesByTournamentId(String tournamentId) throws ExecutionException, InterruptedException {
//         List<Match> matches = new ArrayList<>();
//         Query matchesQuery = db.collection("matches").whereEqualTo("tournamentId", tournamentId);
//         ApiFuture<QuerySnapshot> future = matchesQuery.get();
//         List<QueryDocumentSnapshot> documents = future.get().getDocuments();

//         for (DocumentSnapshot document : documents) {
//             if (document.exists()) {
//                 matches.add(document.toObject(Match.class));
//             }
//         }

//         return matches;
//     }

//     // New method to delete a tournament by ID and associated matches
//     public void deleteTournament(String tournamentId) throws ExecutionException, InterruptedException {
//         // First, delete the tournament
//         DocumentReference tournamentRef = db.collection("tournaments").document(tournamentId);
//         ApiFuture<WriteResult> deleteTournamentFuture = tournamentRef.delete();

//         // Log the result of the delete operation
//         deleteTournamentFuture.addListener(() -> {
//             try {
//                 System.out.println("Tournament deleted successfully at: " + deleteTournamentFuture.get().getUpdateTime());
//             } catch (InterruptedException | ExecutionException e) {
//                 System.err.println("Error deleting tournament: " + e.getMessage());
//             }
//         }, Runnable::run);

//         // Then, delete the associated matches
//         Query matchesQuery = db.collection("matches").whereEqualTo("tournamentId", tournamentId);
//         ApiFuture<QuerySnapshot> matchesFuture = matchesQuery.get();
//         List<QueryDocumentSnapshot> documents = matchesFuture.get().getDocuments();

//         for (DocumentSnapshot document : documents) {
//             DocumentReference matchRef = db.collection("matches").document(document.getId());
//             ApiFuture<WriteResult> deleteMatchFuture = matchRef.delete();

//             // Log the result of each match delete operation
//             deleteMatchFuture.addListener(() -> {
//                 try {
//                     System.out.println("Match with ID: " + document.getId() + " deleted successfully at: " + deleteMatchFuture.get().getUpdateTime());
//                 } catch (InterruptedException | ExecutionException e) {
//                     System.err.println("Error deleting match: " + e.getMessage());
//                 }
//             }, Runnable::run);
//         }
//     }
// }