package com.projectshowdown.service;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.zxing.WriterException;
import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.dto.UserMapper;
import com.projectshowdown.entities.Player;
import com.projectshowdown.entities.User;
import com.projectshowdown.exceptions.PlayerNotFoundException;
import com.projectshowdown.service.FirestoreService.WriteOperation;
import com.projectshowdown.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

  private static final String USERS_COLLECTION = "users";
  private static final String EMAIL_FIELD = "email";
  private static final String ROLE_FIELD = "role";
  private static final String PLAYER_ROLE = "player";

  private static final String CSV_PATH = "C:\\Users\\coben\\OneDrive - Singapore Management University\\Uni - Year 2 Sem 1\\CS203 Collaborative Software Development\\Project\\tenniselo.csv";
  private static final String FIXED_PASSWORD = "$2a$12$NLiiv7gVsA1ltsI1tux.xuE8kEKfAmIHIkloVXwqxHXArgfiJ1XoK";

  private static final int VERIFICATION_CODE_MAX = 900000;
  private static final int VERIFICATION_CODE_MIN = 100000;

  @Autowired
  private TwoFactorAuthService twoFactorAuthService;

  @Autowired
  private FirestoreService firestoreService;

  public UserService() {
    super();
  }

  // Method to load user by username
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    try {
      List<QueryDocumentSnapshot> documents = firestoreService.queryCollection(USERS_COLLECTION, EMAIL_FIELD,
          email);
      if (documents.isEmpty()) {
        throw new UsernameNotFoundException("User not found: " + email);
      }

      DocumentSnapshot document = documents.get(0);
      return org.springframework.security.core.userdetails.User.withUsername(email)
          .password(document.getString("password"))
          .authorities(document.getString(ROLE_FIELD))
          .build();
    } catch (InterruptedException | ExecutionException e) {
      throw new UsernameNotFoundException("Error retrieving user data", e);
    }
  }

  // Method to get registered users
  public List<User> getRegisteredUsers(List<String> listOfUserIds) throws ExecutionException, InterruptedException {
    List<User> response = new ArrayList<>();
    for (String userId : listOfUserIds) {
      response.add(UserMapper.toUser(getUser(userId)));
    }
    return response;
  }

  // Method to get all players
  public List<UserDTO> getAllPlayers() throws ExecutionException, InterruptedException {
    List<QueryDocumentSnapshot> documents = firestoreService.queryCollection(USERS_COLLECTION, ROLE_FIELD,
        PLAYER_ROLE);
    List<UserDTO> players = new ArrayList<>();

    for (DocumentSnapshot document : documents) {
      if (document.exists()) {
        UserDTO currentPlayer = document.toObject(UserDTO.class);
        currentPlayer.setId(document.getId());
        players.add(currentPlayer);
      }
    }
    return players;
  }

  // Method to get userId by email
  public String getUserIdByEmail(String email) throws ExecutionException, InterruptedException {
    List<QueryDocumentSnapshot> documents = firestoreService.queryCollection(USERS_COLLECTION, EMAIL_FIELD, email);
    if (documents.isEmpty()) {
      throw new UsernameNotFoundException("User not found with email: " + email);
    }
    return documents.get(0).getId();
  }

  // Method to get specific player from firebase using userId
  public UserDTO getUser(String userId) throws ExecutionException, InterruptedException {
    // Retrieves the document from Firestore for the given userId
    DocumentSnapshot document = firestoreService.getDocument(USERS_COLLECTION, userId);
    
    // Check if the document exists; throw an exception if not found
    if (!document.exists()) {
      throw new PlayerNotFoundException(userId);
    }

    // Map the Firestore document to a UserDTO object
    UserDTO userToReturn = document.toObject(UserDTO.class);
    
    // Set the userId explicitly, as it's not included in the document data
    userToReturn.setId(userId);

    return userToReturn;
  }

  // Method to add a new player document to the 'users' collection
  public String createUser(User userData) throws ExecutionException, InterruptedException {
    if (firestoreService.exists(USERS_COLLECTION, EMAIL_FIELD, userData.getEmail())) {
      return "A user account with the email " + userData.getEmail() + " already exists!";
    }

    // Prepare user data
    userData.setVerificationCode(generateVerificationCode());
    userData.setVerificationCodeExpiresAt(DateTimeUtils.toEpochSeconds(LocalDateTime.now().plusMinutes(15)));
    userData.setEnabled(false);

    if (userData.getOrganizerDetails() != null) {
      userData.getOrganizerDetails().setVerified(false);
    }

    UserDTO userDTO = UserMapper.toUserDTO(userData);
    String generatedId = firestoreService.createDocument(USERS_COLLECTION, userDTO);
    userDTO.setId(generatedId);

    return "User created successfully with ID: " + generatedId;
  }

  // Method to update a player's document in the 'players' collection
  public String updateUser(String userId, Map<String, Object> userData)
      throws ExecutionException, InterruptedException {
    if (!firestoreService.documentExists(USERS_COLLECTION, userId)) {
      throw new PlayerNotFoundException("User with ID: " + userId + " does not exist.");
    }

    // Check if "organizerDetails" contains "verified" and remove it if present
    if (userData.containsKey("organizerDetails")) {
      @SuppressWarnings("unchecked")
      Map<String, Object> organizerDetails = (Map<String, Object>) userData.get("organizerDetails");
      // Remove the "verified" field if it's present
      if (organizerDetails.containsKey("verified")) {
        organizerDetails.put("verified", false);
      }
    }

    Map<String, Object> filteredUpdates = userData.entrySet().stream()
        .filter(entry -> entry.getValue() != null)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    firestoreService.updateDocument(USERS_COLLECTION, userId, filteredUpdates);
    return "User with ID: " + userId + " updated successfully";
  }

  // Method to verify organizer account
  public String verifyOrganizer(String userId) throws ExecutionException, InterruptedException {
    DocumentSnapshot document = firestoreService.getDocument(USERS_COLLECTION, userId);
    if (!document.exists()) {
      throw new PlayerNotFoundException("User with ID: " + userId + " does not exist.");
    }
    if (document.get("organizerDetails") == null) {
      return "This is not an Organizer account!";
    }

    Map<String, Object> updates = new HashMap<>();
    updates.put("organizerDetails.verified", true);
    updates.put("organizerDetails.dateVerified",
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

    firestoreService.updateDocument(USERS_COLLECTION, userId, updates);
    return "Organizer with ID: " + userId + " has been verified successfully";
  }

  // Method to delete player based on userId
  public String deletePlayer(String userId) throws ExecutionException, InterruptedException {
    if (!firestoreService.documentExists(USERS_COLLECTION, userId)) {
      throw new PlayerNotFoundException(userId);
    }

    firestoreService.deleteDocument(USERS_COLLECTION, userId);
    return "Player with the id:" + userId + " successfully deleted";
  }

  public String enableTwoFactorAuth(String userid)
      throws ExecutionException, InterruptedException, IOException, WriterException {
    UserDTO user = getUser(userid);
    String secret = twoFactorAuthService.generateSecretKey();
    user.setTwoFactorSecret(secret);
    updateUser(userid, UserMapper.toMap(user));
    String qrCodeUri = twoFactorAuthService.generateQrCodeImageUri(secret);
    return twoFactorAuthService.generateQrCodeImage(qrCodeUri);
  }

  public String massImport() {
    List<WriteOperation> operations = new ArrayList<>();

    try (Scanner sc = new Scanner(new File(CSV_PATH), "UTF-8")) {
      sc.nextLine(); // skip header
      sc.useDelimiter(",|\n|\n");

      while (sc.hasNext()) {
        String line = sc.nextLine();
        String[] values = line.split(",");

        UserDTO user = createUserFromCsvValues(values);

        // Create a write operation for each user
        operations.add(new FirestoreService.WriteOperation(
            FirestoreService.OperationType.CREATE,
            USERS_COLLECTION,
            "", // Empty string as ID will be auto-generated
            user,
            null));

        // Process in batches of 500 (Firestore limit)
        if (operations.size() >= 500) {
          firestoreService.batchWrite(operations);
          operations.clear();
        }
      }

      // Process any remaining operations
      if (!operations.isEmpty()) {
        firestoreService.batchWrite(operations);
      }

      return "Successfully imported users";
    } catch (Exception e) {
      return "Import failed: " + e.getMessage();
    }
  }

  private UserDTO createUserFromCsvValues(String[] values) {
    String email = formatEmail(values[1]);
    Player playerDetails = createPlayerFromValues(values);

    return new UserDTO(
        "", // ID will be set later
        email,
        FIXED_PASSWORD,
        "player",
        null,
        playerDetails,
        null,
        null,
        DateTimeUtils.toEpochSeconds(LocalDateTime.now().plusMinutes(15)),
        false);
  }

  private String formatEmail(String value) {
    return value.replaceAll("\\u00A0", "")
        .toLowerCase()
        .trim() + "@gmail.com";
  }

  private Player createPlayerFromValues(String[] values) {
    return new Player(
        Integer.parseInt(values[0]), // rank
        values[1], // name
        values[2], // dob
        Double.parseDouble(values[3]), // elo
        parseDoubleOrNull(values[4]), // hardRaw
        parseDoubleOrNull(values[5]), // clayRaw
        parseDoubleOrNull(values[6]), // grassRaw
        Double.parseDouble(values[7]), // peakAge
        Double.parseDouble(values[8]), // peakElo
        values[9], // country
        "", // bio
        "" // achievements
    );
  }

  private Double parseDoubleOrNull(String value) {
    return value.equals("-") ? null : Double.parseDouble(value);
  }

  public String generateVerificationCode() {
    Random random = new Random();
    int code = random.nextInt(VERIFICATION_CODE_MAX) + VERIFICATION_CODE_MIN;
    return String.valueOf(code);
  }
}
