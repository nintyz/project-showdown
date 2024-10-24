package com.projectshowdown.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.google.zxing.WriterException;
import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.dto.UserMapper;
import com.projectshowdown.entities.Player;
import com.projectshowdown.entities.User;
import com.projectshowdown.exceptions.PlayerNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

@Service
public class UserService implements UserDetailsService {

  @Autowired
  private TwoFactorAuthService twoFactorAuthService;

  public UserService() {
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
        String password = document.getString("password");
        return org.springframework.security.core.userdetails.User.withUsername(email)
            .password(password) // Hashed password
            .authorities(document.getString("role"))
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

  public List<UserDTO> getAllPlayers() throws ExecutionException, InterruptedException {
    Firestore db = getFirestore();
    Query playersCollection = db.collection("users").whereEqualTo("role", "player");
    ApiFuture<QuerySnapshot> future = playersCollection.get();
    List<QueryDocumentSnapshot> documents = future.get().getDocuments();

    // Prepare a list to hold each document's data
    List<UserDTO> players = new ArrayList<>();

    // Iterate through the documents and add their data to the list
    for (DocumentSnapshot document : documents) {
      if (document.exists()) {
        // Add document data to the list
        UserDTO currentPlayer = document.toObject(UserDTO.class);
        // set id.
        currentPlayer.setId(document.getId());
        players.add(currentPlayer);

      }
    }

    return players; // Return the list of players
  }

  public String getUserIdByEmail(String email) throws ExecutionException, InterruptedException {
    Firestore db = getFirestore();
    Query query = db.collection("users").whereEqualTo("email", email);
    ApiFuture<QuerySnapshot> future = query.get();

    List<QueryDocumentSnapshot> documents = future.get().getDocuments();

    if (!documents.isEmpty()) {
      return documents.get(0).getId();
    } else {
      throw new UsernameNotFoundException("User not found with email: " + email);
    }
  }

  // Method to get specific player from firebase.
  public UserDTO getPlayer(String userId) throws ExecutionException, InterruptedException {
    Firestore db = getFirestore();
    DocumentReference documentReference = db.collection("users").document(userId);
    ApiFuture<DocumentSnapshot> future = documentReference.get();
    DocumentSnapshot document = future.get();

    // If the document exists, convert it to a Player object
    if (document.exists()) {
      // You can directly map the Firestore data to a Player class
      UserDTO userToReturn = document.toObject(UserDTO.class);
      userToReturn.setId(userId);
      return userToReturn;
    } else {
      // Document doesn't exist, return null or handle it based on your needs
      throw new PlayerNotFoundException(userId);
    }
  }

  // Method to add a new player document to the 'users' collection
  public String addPlayer(User userData) throws ExecutionException, InterruptedException {
    Firestore db = getFirestore();
    // Generate a new document reference with a random ID
    DocumentReference docRef = db.collection("users").document();
    // Get the generated document ID
    String generatedId = docRef.getId();

    // Convert User object to UserDTO and set the generated ID
    UserDTO userDTO = UserMapper.toUserDTO(userData);
    userDTO.setId(generatedId);

    // Save the updated UserDTO to Firestore
    ApiFuture<WriteResult> writeResult = docRef.set(userDTO);

    // Return success message with timestamp
    return "Player created successfully with ID: " + generatedId + " at: " + writeResult.get().getUpdateTime();
  }

  public String massImport() {
    Firestore db = getFirestore();
    CollectionReference usersCollection = db.collection("users");

    try (Scanner sc = new Scanner(new File(
        "C:\\Users\\coben\\OneDrive - Singapore Management University\\Uni - Year 2 Sem 1\\CS203 Collaborative Software Development\\Project\\tenniselo.csv"),
        "UTF-8")) {
      sc.nextLine();// skip header
      sc.useDelimiter(",|\n|\n");

      while (sc.hasNext()) {
        String line = sc.nextLine(); // extract current row
        String[] values = line.split(","); // split row to tokens

        String email = values[1].replaceAll("\\u00A0", "").toLowerCase();
        email += "@gmail.com";
        String fixedPassword = "$2a$12$NLiiv7gVsA1ltsI1tux.xuE8kEKfAmIHIkloVXwqxHXArgfiJ1XoK";

        int rank = Integer.parseInt(values[0]);
        String name = values[1];
        String dob = values[2];
        Double elo = Double.parseDouble(values[3]);
        Double hardRaw = values[4].equals("-") ? null : Double.parseDouble(values[4]);
        Double clayRaw = values[5].equals("-") ? null : Double.parseDouble(values[5]);
        Double grassRaw = values[6].equals("-") ? null : Double.parseDouble(values[6]);
        Double peakAge = Double.parseDouble(values[7]);
        Double peakElo = Double.parseDouble(values[8]);
        String country = values[9];
        String bio = "";
        String achievements = "";

        Player currentRowPlayerDetails = new Player(rank, name, dob, elo, hardRaw, clayRaw, grassRaw, peakAge, peakElo, country, bio, achievements);

        UserDTO currentRowUser = new UserDTO("", email, fixedPassword, "player", null, currentRowPlayerDetails);

        DocumentReference docRef = usersCollection.document(); // Create a new document reference with a unique ID
        currentRowUser.setId(docRef.getId());
        docRef.set(currentRowUser).addListener(() -> {

        }, Runnable::run);
      }

      while (sc.hasNext()) {
        System.out.println("echo: " + sc.nextLine());
      }
      System.out.println("]'");

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    return "success";

  }

  // Method to update a player's document in the 'players' collection
  public String updatePlayer(String userId, User userData) throws ExecutionException, InterruptedException {
    Firestore db = getFirestore();

    // Check if the user document exists
    DocumentReference docRef = db.collection("users").document(userId);
    ApiFuture<DocumentSnapshot> future = docRef.get();
    DocumentSnapshot document = future.get();

    if (!document.exists()) {
      throw new PlayerNotFoundException("User with ID: " + userId + " does not exist.");
    }

    // Convert User object to UserDTO
    UserDTO userDTO = UserMapper.toUserDTO(userData);

    // Update the user document
    ApiFuture<WriteResult> writeResult = docRef.set(userDTO);

    // Return success message
    return "User with ID: " + userId + " updated successfully at: " + writeResult.get().getUpdateTime();
  }

  public String deletePlayer(String userId) throws ExecutionException, InterruptedException {
    Firestore db = getFirestore();
    DocumentReference docRef = db.collection("users").document(userId);

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

  public String enableTwoFactorAuth(String userid)
      throws ExecutionException, InterruptedException, IOException, WriterException {
    UserDTO user = getPlayer(userid);
    String secret = twoFactorAuthService.generateSecretKey();
    user.setTwoFactorSecret(secret);
    updatePlayer(userid, UserMapper.toUser(user));
    String qrCodeUri = twoFactorAuthService.generateQrCodeImageUri(secret);
    return twoFactorAuthService.generateQrCodeImage(qrCodeUri);
  }
}
