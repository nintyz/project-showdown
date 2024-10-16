// package com.projectshowdown.service;

// import com.google.api.core.ApiFuture;
// import com.google.cloud.firestore.DocumentReference;
// import com.google.cloud.firestore.DocumentSnapshot;
// import com.google.cloud.firestore.Firestore;
// import com.google.cloud.firestore.Query;
// import com.google.cloud.firestore.QueryDocumentSnapshot;
// import com.google.cloud.firestore.QuerySnapshot;
// import com.google.firebase.cloud.FirestoreClient;
// import com.projectshowdown.user.Match;

// import org.springframework.stereotype.Service;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.concurrent.ExecutionException;

// @Service
// public class CustomMatchesDetailsService {

//     private final Firestore db;

//     public CustomMatchesDetailsService() {
//         db = FirestoreClient.getFirestore();
//     }

//     // Method to save a match to Firestore
//     public String saveMatch(Match match) throws ExecutionException, InterruptedException {
//         DocumentReference docRef = db.collection("matches").document(match.getMatchId());
//         ApiFuture<WriteResult> writeResult = docRef.set(match);
//         return "Match saved successfully at: " + writeResult.get().getUpdateTime();
//     }

//     // Method to delete a match from Firestore
//     public String deleteMatch(String matchId) throws ExecutionException, InterruptedException {
//         DocumentReference docRef = db.collection("matches").document(matchId);
//         ApiFuture<WriteResult> deleteResult = docRef.delete();
        
//         return "Match with ID: " + matchId + " deleted successfully at: " + deleteResult.get().getUpdateTime();
//     }

//     // Method to retrieve matches from Firestore
//     public List<Match> retrieveMatches() throws ExecutionException, InterruptedException {
//         List<Match> matches = new ArrayList<>();
//         Query query = db.collection("matches");
//         ApiFuture<QuerySnapshot> future = query.get();
//         List<QueryDocumentSnapshot> documents = future.get().getDocuments();

//         for (DocumentSnapshot document : documents) {
//             if (document.exists()) {
//                 matches.add(document.toObject(Match.class));
//             }
//         }

//         return matches;
//     }
// }