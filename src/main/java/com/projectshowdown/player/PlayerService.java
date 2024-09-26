package com.projectshowdown.player;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.WriteResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.projectshowdown.player.PlayerNotFoundException;;

@Service
public class PlayerService {

    // Helper method to get Firestore instance
    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

  public List<Map<String, Object>> getAllPlayers() throws ExecutionException, InterruptedException {
    Firestore db = getFirestore();
    CollectionReference playersCollection = db.collection("players");
    ApiFuture<QuerySnapshot> future = playersCollection.get();
    List<QueryDocumentSnapshot> documents = future.get().getDocuments();

    // Prepare a list to hold each document's data
    List<Map<String, Object>> players = new ArrayList<>();

    // Iterate through the documents and add their data to the list
    for (DocumentSnapshot document : documents) {
      if (document.exists()) {
        players.add(document.getData()); // Add document data to the list
      }
    }

    return players; // Return the list of players
  }

  // Method to get specific player from firebase.
  public Player getPlayer(int playerId) throws ExecutionException, InterruptedException {
    Firestore db = getFirestore();
    DocumentReference documentReference = db.collection("players").document(Integer.toString(playerId));
    ApiFuture<DocumentSnapshot> future = documentReference.get();
    DocumentSnapshot document = future.get();

    // If the document exists, convert it to a Player object
    if (document.exists()) {
      // You can directly map the Firestore data to a Player class
      Player playerToReturn = document.toObject(Player.class);
      playerToReturn.setId(playerId);
      return playerToReturn;
    } else {
      // Document doesn't exist, return null or handle it based on your needs
      throw new PlayerNotFoundException(playerId);
    }
  }

  // Method to add a new player document to the 'players' collection
  public String addPlayer(Player playerData) throws ExecutionException, InterruptedException {
    Firestore db = getFirestore();
    // count players to set as id
    ApiFuture<QuerySnapshot> query = db.collection("players").get();
    int playerCount = query.get().getDocuments().size();
    playerData.setId(playerCount);

    DocumentReference docRef = db.collection("players").document(Integer.toString(playerCount));
    ApiFuture<WriteResult> writeResult = docRef.set(playerData);
    return "Player created successfully at: " + writeResult.get().getUpdateTime();
  }

  // Method to update a player's document in the 'players' collection
  public String updatePlayer(String id, Player playerData) throws ExecutionException, InterruptedException {
    Firestore db = getFirestore();
    DocumentReference docRef = db.collection("players").document(id);

    // Update player fields in Firebase
    ApiFuture<WriteResult> writeResult = docRef.set(playerData);

    // Return update time after successful update
    return "Player updated successfully at: " + writeResult.get().getUpdateTime();
  }

  public String deletePlayer(int playerId) throws ExecutionException, InterruptedException {
    Firestore db = getFirestore();  
    DocumentReference docRef = db.collection("players").document(Integer.toString(playerId));

    // Check if the player document exists
    ApiFuture<DocumentSnapshot> future = docRef.get();
    DocumentSnapshot document = future.get();

    if (!document.exists()) {
      throw new PlayerNotFoundException(playerId);
    }

    // Asynchronously delete the document
    ApiFuture<WriteResult> writeResult = docRef.delete();

    return "Player with the id:" + playerId + " successfully deleted at: " + writeResult.get().getUpdateTime();
  }
}
