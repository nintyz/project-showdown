package com.projectshowdown.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.zxing.WriterException;
import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.dto.UserMapper;
import com.projectshowdown.entities.Player;
import com.projectshowdown.entities.User;
import com.projectshowdown.exceptions.PlayerNotFoundException;
import com.projectshowdown.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

  public static final String USER_DB = "users";
  public static final String EMAIL_FIELD = "email";
  public static final String PASSWORD_FIELD = "password";
  public static final String ROLE_FIELD = "role";
  public static final String ORGANIZER_DETAILS_FIELD = "organizerDetails";
  public static final String TWO_FACTOR_SECRET_FIELD = "twoFactorSecret";

  @Autowired
  private TwoFactorAuthService twoFactorAuthService;

  /**
   * Loads user details based on the provided email address.
   *
   * @param email The email of the user to load.
   * @return A UserDetails object containing the user's credentials and
   *         authorities.
   * @throws UsernameNotFoundException If no user with the given email is found or
   *                                   an error occurs while retrieving data.
   */
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Firestore db = FirestoreClient.getFirestore();
    ApiFuture<QuerySnapshot> future = db.collection(USER_DB).whereEqualTo(EMAIL_FIELD, email).get();
    try {
      QuerySnapshot querySnapshot = future.get();
      if (!querySnapshot.isEmpty()) {
        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
        String password = document.getString(PASSWORD_FIELD);
        return org.springframework.security.core.userdetails.User.withUsername(email)
            .password(password)
            .authorities(document.getString(ROLE_FIELD))
            .build();
      } else {
        throw new UsernameNotFoundException("User not found: " + email);
      }
    } catch (InterruptedException | ExecutionException e) {
      throw new UsernameNotFoundException("Error retrieving user data", e);
    }
  }

  private Firestore getFirestore() {
    return FirestoreClient.getFirestore();
  }

  /**
   * Retrieves a list of User Objects from a list of IDs
   * This is used for getting the registered users in a tournament.
   *
   * @param listOfUserIds A list of user IDs to retrieve.
   * @return A list of User objects corresponding to the provided IDs.
   * @throws ExecutionException   If an error occurs during the asynchronous
   *                              Firestore operation.
   * @throws InterruptedException If the operation is interrupted.
   */
  public List<User> getRegisteredUsers(List<String> listOfUserIds) throws ExecutionException, InterruptedException {
    List<User> response = new ArrayList<>();
    for (String userId : listOfUserIds) {
      response.add(UserMapper.toUser(getUser(userId)));
    }
    return response;
  }

  /**
   * Retrieves all users with the specified role from the Firestore database.
   *
   * @param role The role to filter users by.
   * @return A list of UserDTO objects with the specified role.
   * @throws ExecutionException   If an error occurs during the asynchronous
   *                              Firestore operation.
   * @throws InterruptedException If the operation is interrupted.
   */
  public List<UserDTO> getAllUsersByRole(String role) throws ExecutionException, InterruptedException {
    Firestore db = getFirestore();
    Query playersCollection = db.collection(USER_DB).whereEqualTo(ROLE_FIELD, role);
    ApiFuture<QuerySnapshot> future = playersCollection.get();
    List<QueryDocumentSnapshot> documents = future.get().getDocuments();
    List<UserDTO> users = new ArrayList<>();
    for (DocumentSnapshot document : documents) {
      if (document.exists()) {
        UserDTO currentUser = document.toObject(UserDTO.class);
        currentUser.setId(document.getId());
        users.add(currentUser);
      }
    }
    return users;
  }

  /**
   * Retrieves a user's ID based on their email address.
   *
   * @param email The email address of the user.
   * @return The ID of the user.
   * @throws ExecutionException        If an error occurs during the asynchronous
   *                                   Firestore operation.
   * @throws InterruptedException      If the operation is interrupted.
   * @throws UsernameNotFoundException If no user with the given email is found.
   */
  public String getUserIdByEmail(String email) throws ExecutionException, InterruptedException {
    Firestore db = getFirestore();
    Query query = db.collection(USER_DB).whereEqualTo(EMAIL_FIELD, email);
    ApiFuture<QuerySnapshot> future = query.get();
    List<QueryDocumentSnapshot> documents = future.get().getDocuments();
    if (!documents.isEmpty()) {
      return documents.get(0).getId();
    } else {
      throw new UsernameNotFoundException("User not found with email: " + email);
    }
  }

  /**
   * Retrieves a user's details based on their ID.
   *
   * @param userId The ID of the user to retrieve.
   * @return A UserDTO object containing the user's details.
   * @throws ExecutionException      If an error occurs during the asynchronous
   *                                 Firestore operation.
   * @throws InterruptedException    If the operation is interrupted.
   * @throws PlayerNotFoundException If no user with the given ID is found.
   */
  public UserDTO getUser(String userId) throws ExecutionException, InterruptedException {
    Firestore db = getFirestore();
    DocumentReference documentReference = db.collection(USER_DB).document(userId);
    ApiFuture<DocumentSnapshot> future = documentReference.get();
    DocumentSnapshot document = future.get();
    if (document.exists()) {
      UserDTO userToReturn = document.toObject(UserDTO.class);
      userToReturn.setId(userId);
      return userToReturn;
    } else {
      throw new PlayerNotFoundException(userId);
    }
  }

  /**
   * Checks if a user with the specified email already exists in the Firestore
   * database.
   *
   * @param email The email address to check.
   * @return True if the email exists, false otherwise.
   * @throws ExecutionException   If an error occurs during the asynchronous
   *                              Firestore operation.
   * @throws InterruptedException If the operation is interrupted.
   */
  public boolean checkEmailExists(String email) throws ExecutionException, InterruptedException {
    Firestore db = getFirestore();
    ApiFuture<QuerySnapshot> future = db.collection(USER_DB).whereEqualTo(EMAIL_FIELD, email).get();
    return !future.get().isEmpty();
  }

  /**
   * Creates a new user in the Firestore database.
   *
   * @param userData A User object containing the details of the new user.
   * @return The ID of the newly created user or an error message if the email
   *         already exists.
   * @throws ExecutionException   If an error occurs during the asynchronous
   *                              Firestore operation.
   * @throws InterruptedException If the operation is interrupted.
   */
  public String createUser(User userData) throws ExecutionException, InterruptedException {
    Firestore db = getFirestore();
    if (checkEmailExists(userData.getEmail())) {
      return "A user account with the email " + userData.getEmail() + " already exists!";
    }
    DocumentReference docRef = db.collection(USER_DB).document();
    String generatedId = docRef.getId();
    userData.setVerificationCode(generateVerificationCode());
    userData.setVerificationCodeExpiresAt(DateTimeUtils.toEpochSeconds(LocalDateTime.now().plusMinutes(15)));
    userData.setEnabled(false);
    if (userData.getOrganizerDetails() != null) {
      userData.getOrganizerDetails().setDateVerified(null);
    }
    UserDTO userDTO = UserMapper.toUserDTO(userData);
    userDTO.setId(generatedId);
    docRef.set(userDTO);
    return generatedId;
  }

  /**
   * Updates an existing user's details in the Firestore database.
   *
   * @param userId   The ID of the user to update.
   * @param userData A map containing the fields to update and their new values.
   *                 I used map because if I were to convert it into a user
   *                 object, it would delete any detail if the front end or API
   *                 misses 1 field. Map allows the back end to not touch missing
   *                 fields.
   * @return The ID of the updated user.
   * @throws ExecutionException      If an error occurs during the asynchronous
   *                                 Firestore operation.
   * @throws InterruptedException    If the operation is interrupted.
   * @throws PlayerNotFoundException If no user with the given ID is found.
   */
  public String updateUser(String userId, Map<String, Object> userData)
      throws ExecutionException, InterruptedException {
    Firestore db = getFirestore();
    if (userData.containsKey(EMAIL_FIELD) && checkEmailExists((String) userData.get(EMAIL_FIELD))) {
      return "A user account with the email " + userData.get(EMAIL_FIELD) + " already exists!";
    }
    if (userData.containsKey(PASSWORD_FIELD)) {
      BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
      userData.put(PASSWORD_FIELD, passwordEncoder.encode((String) userData.get(PASSWORD_FIELD)));
    }
    DocumentReference docRef = db.collection(USER_DB).document(userId);
    if (!docRef.get().get().exists()) {
      throw new PlayerNotFoundException("User with ID: " + userId + " does not exist.");
    }
    if (userData.get(ORGANIZER_DETAILS_FIELD) != null) {
      Map<String, Object> organizerDetails = (Map<String, Object>) userData.get(ORGANIZER_DETAILS_FIELD);
      if (organizerDetails.containsKey("verified")) {
        organizerDetails.put("verified", false);
      }
    }
    Map<String, Object> filteredUpdates = userData.entrySet().stream()
        .filter(entry -> entry.getValue() != null)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    docRef.update(filteredUpdates);
    return userId;
  }

  /**
   * Verifies an organizer's account by updating their verification date.
   *
   * @param userId The ID of the organizer to verify.
   * @return A success message if the organizer is verified, or an error message
   *         if not applicable.
   * @throws ExecutionException      If an error occurs during the asynchronous
   *                                 Firestore operation.
   * @throws InterruptedException    If the operation is interrupted.
   * @throws PlayerNotFoundException If no user with the given ID is found.
   */
  public String verifyOrganizer(String userId) throws ExecutionException, InterruptedException {
    Firestore db = getFirestore();
    DocumentReference docRef = db.collection(USER_DB).document(userId);
    DocumentSnapshot document = docRef.get().get();
    if (!document.exists()) {
      throw new PlayerNotFoundException("User with ID: " + userId + " does not exist.");
    }
    if (document.get(ORGANIZER_DETAILS_FIELD) == null) {
      return "This is not an Organizer account!";
    }
    Map<String, Object> updates = new HashMap<>();
    updates.put("organizerDetails.dateVerified",
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
    docRef.update(updates);
    return "Organizer with ID: " + userId + " has been verified successfully.";
  }

  /**
   * Deletes a user from the Firestore database based on their ID.
   *
   * @param userId The ID of the user to delete.
   * @return A success message containing the user's ID and deletion timestamp.
   * @throws ExecutionException      If an error occurs during the asynchronous
   *                                 Firestore operation.
   * @throws InterruptedException    If the operation is interrupted.
   * @throws PlayerNotFoundException If no user with the given ID is found.
   */
  public String deletePlayer(String userId) throws ExecutionException, InterruptedException {
    Firestore db = getFirestore();
    DocumentReference docRef = db.collection(USER_DB).document(userId);
    if (!docRef.get().get().exists()) {
      throw new PlayerNotFoundException(userId);
    }
    docRef.delete();
    return "Player with ID: " + userId + " successfully deleted.";
  }

  /**
   * Enables two-factor authentication for a user by generating a secret key and
   * updating their details.
   *
   * @param userId The ID of the user to enable two-factor authentication for.
   * @return A URI for the QR code image or an error message if generation fails.
   * @throws ExecutionException   If an error occurs during the asynchronous
   *                              Firestore operation.
   * @throws InterruptedException If the operation is interrupted.
   * @throws IOException          If an error occurs during QR code generation.
   * @throws WriterException      If an error occurs while writing the QR code.
   */
  public String enableTwoFactorAuth(String userId)
      throws ExecutionException, InterruptedException, IOException, WriterException {
    UserDTO user = getUser(userId);
    String secret = twoFactorAuthService.generateSecretKey();
    user.setTwoFactorSecret(secret);
    updateUser(userId, Map.of(TWO_FACTOR_SECRET_FIELD, secret));
    return twoFactorAuthService.generateQrCodeImage(twoFactorAuthService.generateQrCodeImageUri(secret));
  }

  /**
   * Imports users in bulk from a CSV file into the Firestore database.
   * This was made to utilize an excel file of real tennis players we found online
   * However, It floods our database with 490+ players and makes testing
   * difficult.
   *
   * @return A success message upon completing the import.
   */
  public String massImport() {
    Firestore db = getFirestore();
    CollectionReference usersCollection = db.collection(USER_DB);
    try (Scanner sc = new Scanner(new File("path/to/your/file.csv"), "UTF-8")) {
      sc.nextLine();
      while (sc.hasNext()) {
        String[] values = sc.nextLine().split(",");
        String email = values[1].trim().toLowerCase() + "@gmail.com";
        UserDTO user = new UserDTO("", values[1], "", email, "password123", "player",
            null, new Player(Integer.parseInt(values[0]), values[2],
                Double.parseDouble(values[3]), Double.parseDouble(values[4]),
                Double.parseDouble(values[5]), values[6], "", ""),
            null, null, DateTimeUtils.toEpochSeconds(LocalDateTime.now().plusMinutes(15)), false);
        DocumentReference docRef = usersCollection.document();
        user.setId(docRef.getId());
        docRef.set(user);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return "success";
  }

  /**
   * Generates a random verification code.
   *
   * @return A 6-digit random verification code as a string.
   */
  public String generateVerificationCode() {
    return String.valueOf(new Random().nextInt(900000) + 100000);
  }
}
