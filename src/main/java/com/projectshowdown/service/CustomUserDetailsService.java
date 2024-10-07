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
import com.projectshowdown.exceptions.PlayerNotFoundException;
import com.projectshowdown.user.User;
import com.projectshowdown.user.UserDTO;
import com.projectshowdown.user.UserMapper;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  public CustomUserDetailsService() {
    super();

  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Firestore db = FirestoreClient.getFirestore();
    ApiFuture<QuerySnapshot> future = db.collection("users").whereEqualTo("email", email).get();

    try {
      QuerySnapshot querySnapshot = future.get();
      if (!querySnapshot.isEmpty()) {
        // Assuming there is only one user with the given username
        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
        String password = document.getString("password"); // Assuming password is stored
        return org.springframework.security.core.userdetails.User.withUsername(email)
            .password(password) // Hashed password
            .roles("ADMIN") // You can fetch roles from Firestore if needed
            .build();
      } else {
        throw new UsernameNotFoundException("User not found: " + email);
      }
    } catch (InterruptedException | ExecutionException e) {
      throw new UsernameNotFoundException("Error retrieving user data", e);
    }
  }

  // Helper method to get Firestore instance
  private Firestore getFirestore() {
    return FirestoreClient.getFirestore();
  }

  public List<Map<String, Object>> getAllPlayers() throws ExecutionException, InterruptedException {
    Firestore db = getFirestore();
    Query playersCollection = db.collection("users").whereEqualTo("role", "player");
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
  public User getPlayer(int userId) throws ExecutionException, InterruptedException {
    Firestore db = getFirestore();
    DocumentReference documentReference = db.collection("users").document(Integer.toString(userId));
    ApiFuture<DocumentSnapshot> future = documentReference.get();
    DocumentSnapshot document = future.get();

    // If the document exists, convert it to a Player object
    if (document.exists()) {
      // You can directly map the Firestore data to a Player class
      User userToReturn = document.toObject(User.class);
      userToReturn.setId(userId);
      return userToReturn;
    } else {
      // Document doesn't exist, return null or handle it based on your needs
      throw new PlayerNotFoundException(userId);
    }
  }

  // Method to add a new player document to the 'players' collection
  public String addPlayer(User userData) throws ExecutionException, InterruptedException {
    Firestore db = getFirestore();
    // count players to set as id
    ApiFuture<QuerySnapshot> query = db.collection("users").get();
    int userCount = query.get().getDocuments().size();
    userData.setId(userCount);

    DocumentReference docRef = db.collection("users").document(Integer.toString(userCount));
    UserDTO userDTO = UserMapper.toUserDTO(userData);
    ApiFuture<WriteResult> writeResult = docRef.set(userDTO);
    return "Player created successfully at: " + writeResult.get().getUpdateTime();

  }

  // Method to update a player's document in the 'players' collection
  public String updatePlayer(String id, User playerData) throws ExecutionException, InterruptedException {
    Firestore db = getFirestore();
    DocumentReference docRef = db.collection("users").document(id);

    // Update player fields in Firebase
    UserDTO userDTO = UserMapper.toUserDTO(playerData);
    ApiFuture<WriteResult> writeResult = docRef.set(userDTO);

    // Return update time after successful update
    return "Player updated successfully at: " + writeResult.get().getUpdateTime();
  }

  public String deletePlayer(int userId) throws ExecutionException, InterruptedException {
    Firestore db = getFirestore();
    DocumentReference docRef = db.collection("users").document(Integer.toString(userId));

    // Check if the player document exists
    ApiFuture<DocumentSnapshot> future = docRef.get();
    DocumentSnapshot document = future.get();

    if (!document.exists()) {
      throw new PlayerNotFoundException(userId);
    }

    // Asynchronously delete the document
    ApiFuture<WriteResult> writeResult = docRef.delete();

    return "Player with the id:" + userId + " successfully deleted at: " + writeResult.get().getUpdateTime();
  }
}
