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
import com.google.firebase.cloud.StorageClient;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.dto.UserMapper;
import com.projectshowdown.entities.Match;
import com.projectshowdown.entities.Round;
import com.projectshowdown.entities.Tournament;
import com.projectshowdown.exceptions.TournamentNotFoundException;

import jakarta.mail.MessagingException;

import com.projectshowdown.entities.User;
import com.projectshowdown.events.MatchUpdatedEvent;

@Service
public class TournamentService {
    public static final String TOURNAMENTS_DB = "tournaments";
    public static final String USERS_FIELD = "users";
    public static final String ROUNDS_FIELD = "rounds";
    public static final String MATCHES_FIELD = "matches";
    public static final String PLAYER_1_ID_FIELD = "player1Id";
    public static final String PLAYER_2_ID_FIELD = "player2Id";
    public static final String ORGANIZER_ROLE = "organizer";
    public static final String ORGANIZER_ID_FIELD = "organizerId";
    public static final String STATUS_FIELD = "status";
    @Autowired
    UserService userService;

    @Autowired
    MatchService matchService;

    @Autowired
    NotificationService notificationService;

    /**
     * Retrieves the Firestore database instance.
     *
     * @return The Firestore instance.
     */
    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    /**
     * Retrieves all tournaments from Firestore.
     *
     * @return A list of all Tournament objects.
     * @throws ExecutionException   If an error occurs during the asynchronous
     *                              Firestore operation.
     * @throws InterruptedException If the operation is interrupted.
     */
    public List<Tournament> getAllTournaments() throws ExecutionException, InterruptedException {
        Firestore db = getFirestore();
        QuerySnapshot querySnapshot = db.collection(TOURNAMENTS_DB).get().get();

        // Map the documents directly to Tournament objects
        return querySnapshot.getDocuments().stream()
                .map(document -> {
                    Tournament tournament = document.toObject(Tournament.class);
                    tournament.setId(document.getId());
                    return tournament;
                })
                .collect(Collectors.toList());
    }

    /**
     * Retrieves tournaments organized by a specific organizer.
     *
     * @param organizerId The organizer's ID.
     * @return A list of tournaments organized by the given organizer.
     * @throws ExecutionException   If an error occurs during the asynchronous
     *                              Firestore operation.
     * @throws InterruptedException If the operation is interrupted.
     */
    public List<Tournament> getTournamentsByOrganizerId(String organizerId)
            throws ExecutionException, InterruptedException {
        Firestore db = getFirestore();
        QuerySnapshot querySnapshot = db.collection(TOURNAMENTS_DB).get().get();

        // Map the documents directly to Tournament objects
        return querySnapshot.getDocuments().stream()
                .map(document -> {
                    Tournament tournament = document.toObject(Tournament.class);
                    tournament.setId(document.getId());
                    return tournament;
                })
                .collect(Collectors.toList());
    }

    /**
     * Retrieves tournaments a specific player is registered in.
     *
     * @param userId The ID of the player.
     * @return A list of tournaments the player is registered in.
     * @throws ExecutionException   If an error occurs during the asynchronous
     *                              Firestore operation.
     * @throws InterruptedException If the operation is interrupted.
     */
    public List<Tournament> getTournamentsByPlayerId(String userId) throws ExecutionException, InterruptedException {
        CollectionReference tournaments = getFirestore().collection(TOURNAMENTS_DB);
        Query query = tournaments.whereArrayContains(USERS_FIELD, userId);
        return query.get().get().toObjects(Tournament.class);
    }

    /**
     * Adds a new tournament to Firestore.
     *
     * @param tournament The Tournament object to add.
     * @param userId     The ID of the organizer.
     * @return The ID of the created tournament or an error message.
     * @throws ExecutionException   If an error occurs during the asynchronous
     *                              Firestore operation.
     * @throws InterruptedException If the operation is interrupted.
     */
    public String addTournament(Tournament tournament, String userId) throws ExecutionException, InterruptedException {
        Firestore db = getFirestore();
        DocumentReference docRef = db.collection(TOURNAMENTS_DB).document();
        tournament.setId(docRef.getId());
        tournament.setOrganizerId(userId);

        try {
            docRef.set(tournament);
            return docRef.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error adding tournament: " + e.getMessage();
        }
    }

    /**
     * Retrieves a specific tournament by ID.
     *
     * @param tournamentId The ID of the tournament.
     * @return The Tournament object.
     * @throws ExecutionException          If an error occurs during the
     *                                     asynchronous Firestore operation.
     * @throws InterruptedException        If the operation is interrupted.
     * @throws TournamentNotFoundException If the tournament is not found.
     */
    public Tournament getTournament(String tournamentId) throws ExecutionException, InterruptedException {
        Firestore db = getFirestore();
        DocumentReference docRef = db.collection(TOURNAMENTS_DB).document(tournamentId);
        DocumentSnapshot document = docRef.get().get();
        if (document.exists()) {
            Tournament tournament = document.toObject(Tournament.class);
            tournament.setId(tournamentId);
            return tournament;
        } else {
            throw new TournamentNotFoundException(tournamentId);
        }
    }

    /**
     * Displays a detailed view of a tournament, including rounds and player data.
     *
     * @param tournamentId The ID of the tournament.
     * @return A map representing the tournament details.
     * @throws ExecutionException   If an error occurs during the asynchronous
     *                              Firestore operation.
     * @throws InterruptedException If the operation is interrupted.
     */
    public Map<String, Object> displayTournament(String tournamentId) throws ExecutionException, InterruptedException {
        Firestore db = getFirestore();
        Map<String, Object> response = new HashMap<>();

        // Fetch tournament details
        DocumentSnapshot tournamentSnapshot = db.collection(TOURNAMENTS_DB).document(tournamentId).get().get();
        if (!tournamentSnapshot.exists()) {
            throw new IllegalArgumentException("Tournament not found");
        }
        response.putAll(tournamentSnapshot.getData());

        // Fetch and enrich rounds
        List<Map<String, Object>> rounds = (List<Map<String, Object>>) tournamentSnapshot.get(ROUNDS_FIELD);
        if (rounds != null) {
            List<Map<String, Object>> enrichedRounds = rounds.stream().map(round -> {
                List<String> matchIds = (List<String>) round.get(MATCHES_FIELD);
                List<Map<String, Object>> matchesData = new ArrayList<>();

                if (matchIds != null) {
                    matchIds.forEach(matchId -> {
                        try {
                            DocumentSnapshot matchSnapshot = db.collection(MATCHES_FIELD).document(matchId).get().get();
                            if (matchSnapshot.exists()) {
                                Map<String, Object> matchData = matchSnapshot.getData();
                                enrichMatchWithPlayerData(db, matchData);
                                matchesData.add(matchData);
                            }
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                }
                round.put(MATCHES_FIELD, matchesData);
                return round;
            }).collect(Collectors.toList());
            response.put(ROUNDS_FIELD, enrichedRounds);
        }

        return response;
    }

    /**
     * Enriches a match's data with player information.
     *
     * @param db        The Firestore instance.
     * @param matchData The match data to enrich.
     */
    private void enrichMatchWithPlayerData(Firestore db, Map<String, Object> matchData) {
        try {
            String player1Id = (String) matchData.get(PLAYER_1_ID_FIELD);
            String player2Id = (String) matchData.get(PLAYER_2_ID_FIELD);

            if (player1Id != null) {
                DocumentSnapshot player1Snapshot = db.collection(USERS_FIELD).document(player1Id).get().get();
                matchData.put("player1",
                        player1Snapshot.exists() ? player1Snapshot.getData() : "Player account deleted");
            }
            if (player2Id != null) {
                DocumentSnapshot player2Snapshot = db.collection(USERS_FIELD).document(player2Id).get().get();
                matchData.put("player2",
                        player2Snapshot.exists() ? player2Snapshot.getData() : "Player account deleted");
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates a tournament's details in Firestore.
     *
     * @param tournamentId   The ID of the tournament to update.
     * @param organizerId    The ID of the organizer making the update.
     * @param tournamentData The data to update.
     * @return A success message with the update time or an error message.
     * @throws ExecutionException          If an error occurs during the
     *                                     asynchronous Firestore operation.
     * @throws InterruptedException        If the operation is interrupted.
     * @throws TournamentNotFoundException If the tournament is not found.
     */
    public String updateTournament(String tournamentId, String organizerId, Map<String, Object> tournamentData)
            throws ExecutionException, InterruptedException {
        Firestore db = getFirestore();
        DocumentReference docRef = db.collection(TOURNAMENTS_DB).document(tournamentId);

        // Check if the tournament exists
        DocumentSnapshot document = docRef.get().get();
        if (!document.exists()) {
            throw new TournamentNotFoundException(tournamentId);
        }

        // Ensure organizer permissions
        UserDTO organizer = userService.getUser(organizerId);
        if (!organizer.getRole().equalsIgnoreCase(ORGANIZER_ROLE) || !organizerId.equals(document.get(ORGANIZER_ID_FIELD))) {
            return "You are not authorized to edit this tournament.";
        }

        // Apply updates
        Map<String, Object> filteredUpdates = tournamentData.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // check if the update is to cancel tournament
        if (tournamentData.containsKey(STATUS_FIELD)
                && ((String) tournamentData.get(STATUS_FIELD)).equalsIgnoreCase("Cancelled")) {
            // check if tournament has begun
            Tournament tournament = getTournament(tournamentId);
            if (tournament.inProgress()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "You are not allowed to cancel a tournament that has already begun!");
            }

            // EMAIL NOTIFICATION TO LET REGISTERED PLAYERS KNOW ABOUT ITS CANCELLATION
            // Retrieve the tournament name from the document
            String tournamentName = document.getString("name");

            // Retrieve the list of registered users
            List<String> registeredUsers = (List<String>) document.get(USERS_FIELD);
            for (String userId : registeredUsers) {

                try {
                    UserDTO user = userService.getUser(userId);
                    // Send cancellation notification to each user
                    notificationService.notifyTournamentCancelled(user.getEmail(), tournamentName);
                    System.out.println("Cancellation notification sent to user: " + user.getName());
                } catch (MessagingException e) {
                    System.out.println("Failed to send cancellation notification to user: " + userId);
                    e.printStackTrace();
                }
            }

            docRef.update(filteredUpdates);
            return "Tournament with ID: " + tournamentId + " has been cancelled!";
        }

        ApiFuture<WriteResult> updateWriteResult = docRef.update(filteredUpdates);

        // Return success message with the update time
        return "Tournament with ID: " + tournamentId + " updated successfully at: "
                + updateWriteResult.get().getUpdateTime();
    }

    /**
     * Registers a user for a tournament.
     *
     * @param tournamentId The ID of the tournament.
     * @param userId       The ID of the user to register.
     * @return A success message or error message if registration fails.
     * @throws ExecutionException          If an error occurs during the
     *                                     asynchronous Firestore operation.
     * @throws InterruptedException        If the operation is interrupted.
     * @throws TournamentNotFoundException If the tournament is not found.
     */
    public String registerUser(String tournamentId, String userId) throws ExecutionException, InterruptedException {
        Firestore db = getFirestore();
        DocumentReference docRef = db.collection(TOURNAMENTS_DB).document(tournamentId);

        // Check if the document exists
        DocumentSnapshot document = docRef.get().get();
        if (!document.exists()) {
            throw new TournamentNotFoundException(tournamentId);
        }

        Tournament tournament = getTournament(tournamentId);
        if (tournament.getRounds() != null && !tournament.getRounds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tournament has already begun.");
        }

        UserDTO user = userService.getUser(userId);
        if (!tournament.checkDate(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tournament registration period is over.");
        }
        if (!tournament.checkUserEligibility(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Player MMR is not eligible for this tournament.");
        }

        // Register user
        List<String> users = (List<String>) document.get(USERS_FIELD);
        if (users == null)
            users = new ArrayList<>();
        if (users.contains(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are already registered for this tournament.");
        }

        // Add user to the list and update in Firebase
        users.add(userId);
        docRef.update(USERS_FIELD, users);
        return "Successfully registered.";
    }

    /**
     * Cancels a user's registration for a specific tournament.
     *
     * @param tournamentId The ID of the tournament.
     * @param userId       The ID of the user to unregister.
     * @return A success message with the unregistration timestamp.
     * @throws ExecutionException          If an error occurs during the Firestore
     *                                     operation.
     * @throws InterruptedException        If the operation is interrupted.
     * @throws TournamentNotFoundException If the tournament is not found.
     */
    public String cancelRegistration(String tournamentId, String userId)
            throws ExecutionException, InterruptedException {
        Firestore db = getFirestore();
        DocumentReference docRef = db.collection(TOURNAMENTS_DB).document(tournamentId);

        // Check if the tournament document exists
        DocumentSnapshot document = docRef.get().get();
        if (!document.exists()) {
            throw new TournamentNotFoundException(tournamentId);
        }

        List<String> registeredUsers = (List<String>) document.get(USERS_FIELD);

        // Remove the user if they are registered
        if (registeredUsers != null && registeredUsers.contains(userId)) {
            registeredUsers.remove(userId);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not registered for this event!");
        }

        // Update the 'users' field in Firestore
        WriteResult writeResult = docRef.update(USERS_FIELD, registeredUsers).get();

        return "UserId: " + userId + " has successfully unregistered from tournament: " + tournamentId
                + " at: " + writeResult.getUpdateTime();
    }

    /**
     * Handles the event when a match is updated and progresses the tournament if
     * required.
     *
     * @param event The MatchUpdatedEvent containing details about the updated
     *              match.
     * @throws ExecutionException   If an error occurs during Firestore operations.
     * @throws InterruptedException If the operation is interrupted.
     */
    @EventListener
    public void handleMatchUpdated(MatchUpdatedEvent event) throws ExecutionException, InterruptedException {
        String tournamentId = event.getTournamentId();
        Tournament tournament = getTournament(tournamentId);

        // Get matches in the last round
        List<String> lastRoundMatches = tournament.getRounds().get(tournament.getRounds().size() - 1).getMatches();

        System.out.println("Checking if all matches in the round are completed...");
        if (matchService.checkCurrentRoundCompletion(lastRoundMatches)) {
            System.out.println("All matches completed for this round.");

            // If it is the final round, update the tournament's status and user
            // achievements
            if (lastRoundMatches.size() == 1) {
                Map<String, Object> statusToUpdate = new HashMap<>();
                statusToUpdate.put(STATUS_FIELD, "Ended");
                updateTournament(tournamentId, tournament.getOrganizerId(), statusToUpdate);

                // Update achievements for winner and loser
                updateUserAchievements(tournament, event.getMatch().winnerId(), true);
                updateUserAchievements(tournament, event.getMatch().loserId(), false);

                System.out.println("Final round completed. No further progression needed.");
            } else {
                // Progress to the next round
                String progressionResult = progressTournament(tournamentId);
                System.out.println("Tournament progression result: " + progressionResult);
            }
        } else {
            System.out.println("Not all matches in this round are completed.");
        }
    }

    /**
     * Updates a user's achievements based on their performance in the tournament.
     *
     * @param tournament The tournament where the achievement was earned.
     * @param userId     The ID of the user whose achievements are being updated.
     * @param gold       True if the user won the tournament, false if they were the
     *                   runner-up.
     * @throws ExecutionException   If an error occurs during Firestore operations.
     * @throws InterruptedException If the operation is interrupted.
     */
    public void updateUserAchievements(Tournament tournament, String userId, boolean gold)
            throws ExecutionException, InterruptedException {
        UserDTO user = userService.getUser(userId);

        // Construct the achievement message
        String achievement = "Obtained " + (gold ? "Gold" : "Silver") + " from " + tournament.getName() + ". ";
        Map<String, Object> achievements = new HashMap<>();
        achievements.put("playerDetails.achievements", user.getPlayerDetails().getAchievements() + achievement);

        userService.updateUser(userId, achievements);
    }

    /**
     * Retrieves the list of users who won their matches in the last round.
     *
     * @param matches The list of match IDs from the last round.
     * @return A list of winning User objects.
     * @throws ExecutionException   If an error occurs during Firestore operations.
     * @throws InterruptedException If the operation is interrupted.
     */
    public List<User> getWinningUsers(List<String> matches) throws ExecutionException, InterruptedException {
        List<User> winners = new ArrayList<>();

        for (Match match : matchService.getMatches(matches)) {
            if (!match.isCompleted()) {
                throw new RuntimeException("Matches from the last round are not completed!");
            }
            winners.add(UserMapper.toUser(userService.getUser(match.winnerId())));
        }

        return winners;
    }

    /**
     * Progresses the tournament to the next round if applicable.
     *
     * @param tournamentId The ID of the tournament to progress.
     * @return A message indicating the result of the progression.
     * @throws ExecutionException   If an error occurs during Firestore operations.
     * @throws InterruptedException If the operation is interrupted.
     */

    public String progressTournament(String tournamentId) throws ExecutionException, InterruptedException {
        Tournament tournament = getTournament(tournamentId);
        String nextRoundName = determineNextRoundName(tournament);

        if ("Error".equals(nextRoundName)) {
            return "The tournament has already completed!";
        } else if ("Round 1".equals(nextRoundName)) {
            System.out.println("Initializing tournament with the first round.");
            return initializeTournament(tournament, nextRoundName);
        }

        System.out.println("Generating next round: " + nextRoundName);
        String result = generateNextRound(tournament, nextRoundName);
        System.out.println("Next round generation result: " + result);

        // Save the updated tournament in Firestore
        Firestore db = getFirestore();
        db.collection(TOURNAMENTS_DB).document(tournamentId).set(tournament).get();

        return "Next round processed. Result: " + result;
    }

    /**
     * Determines the name of the next round in the tournament based on the current
     * round count.
     *
     * @param tournament The Tournament object containing current round and user
     *                   details.
     * @return The name of the next round, or "Error" if the tournament has already
     *         completed.
     */
    private String determineNextRoundName(Tournament tournament) {
        int roundCount = tournament.getRounds() != null ? tournament.getRounds().size() : 0;
        int totalUsers = tournament.getUsers().size();

        switch (roundCount) {
            case 0:
                return "Round 1";
            case 1:
                return totalUsers == 32 ? "Round 2" : "QuarterFinals";
            case 2:
                return totalUsers == 32 ? "QuarterFinals" : "Semi Finals";
            case 3:
                return totalUsers == 32 ? "Semi Finals" : "Finals";
            case 4:
                return totalUsers == 32 ? "Finals" : "Error";
            default:
                return "Error";
        }
    }

    /**
     * Initializes the first round of a tournament by generating matches based on
     * player MMR.
     *
     * @param tournament The Tournament object to initialize.
     * @param roundName  The name of the round being initialized (e.g., "Round 1").
     * @return A message indicating success or failure of initialization.
     */
    public String initializeTournament(Tournament tournament, String roundName) {
        try {
            List<User> users = userService.getRegisteredUsers(tournament.getUsers());
            users.sort(Comparator.comparingDouble(user -> user.getPlayerDetails().calculateMMR()));

            if (users.size() != tournament.getNumPlayers()) {
                return "The required amount of registered players have not been met!";
            }

            List<String> matches = generateMatchesWithSeed(tournament, users, roundName, 0);
            addRoundToTournament(tournament, "Initial", matches);

            return matches.size() + " matches have been generated for tournament id " + tournament.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * Generates the matches for the next round in a tournament based on the winners
     * of the previous round.
     *
     * @param tournament The Tournament object to progress.
     * @param roundName  The name of the next round to generate.
     * @return A message indicating the result of the round generation.
     */
    public String generateNextRound(Tournament tournament, String roundName) {
        try {
            List<String> lastRound = getLastRoundMatches(tournament);
            List<User> winners = getWinningUsers(lastRound);
            List<String> matches = generateFollowUpMatches(tournament, winners, roundName, tournament.totalMatches());

            addRoundToTournament(tournament, roundName, matches);

            return matches.size() + " matches have been generated for tournament id " + tournament.getId() + " for the "
                    + roundName;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * Retrieves the matches from the last round of the tournament and sorts them by
     * match ID.
     *
     * @param tournament The Tournament object containing round details.
     * @return A sorted list of match IDs from the last round.
     */
    private List<String> getLastRoundMatches(Tournament tournament) {
        List<String> lastRound = tournament.getRounds().get(tournament.getRounds().size() - 1).getMatches();
        lastRound.sort(Comparator.comparingInt(id -> Integer.parseInt(id.split("_")[1])));
        return lastRound;
    }

    /**
     * Generates matches for the first round of a tournament, seeding the top
     * players based on their MMR.
     *
     * @param tournament   The Tournament object.
     * @param users        A list of users registered for the tournament.
     * @param stage        The stage or round name (e.g., "Round 1").
     * @param totalMatches The total number of matches already generated (used for
     *                     match IDs).
     * @return A list of match IDs generated for the round.
     * @throws ExecutionException   If an error occurs during Firestore operations.
     * @throws InterruptedException If the operation is interrupted.
     */
    private List<String> generateMatchesWithSeed(Tournament tournament, List<User> users, String stage,
            int totalMatches)
            throws ExecutionException, InterruptedException {
        List<String> matches = new ArrayList<>(); // List to store match IDs

        // Define seeded positions. Keys are match positions, values are indices of
        // top-seeded users. Assuming 4 seeded players.
        Map<Integer, Integer> seedPositions = Map.of(
                1, 0,
                users.size() / 2, 1,
                users.size() / 4, 2,
                users.size() / 4 + 1, 3);

        int left = 4; // Points to the strongest non-seeded player, as indexes 0 - 3 are seeded
                      // players
        int right = users.size() - 1; // Points to the weakest non-seeded players
        List<Integer> usedPlayers = new ArrayList<>(); // Track users who have been matched already

        // Loop to generate matches for each pair of players
        for (int i = 1; i <= users.size() / 2; i++) {
            User user1, user2;

            // Check if the current position is a seeded position
            if (seedPositions.containsKey(i)) {
                int seedIndex = seedPositions.get(i);
                user1 = users.get(seedIndex); // If position is seeded, get the index of the seeded player
                user2 = users.get(right); // Pair with weakest user
                usedPlayers.add(seedIndex);
                usedPlayers.add(right--); // Mark players as used and decrement right pointer
            } else {
                // Find the next available players for non-seeded matches
                while (usedPlayers.contains(left))
                    left++; // Move left pointer to retrieve the next weakest player

                user1 = users.get(left);
                usedPlayers.add(left++); // Mark as used and increment left pointer

                while (usedPlayers.contains(right))
                    right--; // Move right pointer to retrieve the next strongest player

                user2 = users.get(right); // Pair with the next weakest user
                usedPlayers.add(right--); // Mark as used and decrement right pointer
            }

            // Create the match between the two selected players
            matches.add(createMatch(tournament, stage, user1, user2, totalMatches + matches.size() + 1));

            // Send email notifications to both players about the match, however dateTime is
            try {
                System.out.println("Sending player match email ....");
                notificationService.notifyPlayerMatched(
                        user1.getEmail(), user1.getName(), user2.getName(),
                        tournament.getName());
                notificationService.notifyPlayerMatched(
                        user2.getEmail(), user2.getName(), user1.getName(),
                        tournament.getName());
            } catch (MessagingException e) {
                // Handle email sending failure
                System.out.println("Failed to send match notification for players: "
                        + user1.getId() + " and " + user2.getId());
                e.printStackTrace();
            }
        }
        return matches; // Return list of match IDs
    }

    private String createMatch(Tournament tournament, String stage, User user1, User user2, int matchIndex)
            throws ExecutionException, InterruptedException {
        String matchId = tournament.getId() + "m_" + matchIndex;
        Match match = new Match(matchId, tournament.getId(), user1.getId(), user2.getId(), 0, 0,
                Math.abs(user1.getPlayerDetails().calculateMMR() - user2.getPlayerDetails().calculateMMR()),
                "TBC", stage, false);
        return matchService.addMatch(match);
    }

    private List<String> generateFollowUpMatches(Tournament tournament, List<User> users, String stage,
            int totalMatches)
            throws ExecutionException, InterruptedException {
        List<String> matches = new ArrayList<>();
        for (int i = 0; i < users.size(); i += 2) {
            matches.add(
                    createMatch(tournament, stage, users.get(i), users.get(i + 1), totalMatches + matches.size() + 1));
        }
        return matches;
    }

    private void addRoundToTournament(Tournament tournament, String roundName, List<String> matches)
            throws ExecutionException, InterruptedException {
        Round newRound = new Round(roundName, matches);
        tournament.getRounds().add(newRound);
        Map<String, Object> toUpdateTournament = new HashMap<String, Object>();
        toUpdateTournament.put(ROUNDS_FIELD, tournament.getRounds());
        toUpdateTournament.put(STATUS_FIELD, "In Progress");
        updateTournament(tournament.getId(), tournament.getOrganizerId(), toUpdateTournament);
    }

    // Upload logo to Firebase Storage
    public String uploadLogoToFirebase(String tournamentId, MultipartFile file)
            throws IOException, ExecutionException, InterruptedException {

        String bucketName = "projectshowdown-df5f2.firebasestorage.app"; // Firebase Storage
                                                                         // bucket name
        String fileName = "tournament-logo/" + tournamentId + ".jpg";

        // Initialize StorageClient with the specified bucket
        StorageClient storageClient = StorageClient.getInstance();
        if (storageClient.bucket(bucketName) == null) {
            throw new IllegalArgumentException("Firebase Storage bucket name is not specified.");
        }

        storageClient.bucket(bucketName).create(fileName, file.getInputStream(), file.getContentType());

        // Generate logo URL
        String logoUrl = String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucketName, fileName.replace("/", "%2F"));

        // Update Firestore with logo URL
        Firestore db = getFirestore();
        DocumentReference docRef = db.collection(TOURNAMENTS_DB).document(tournamentId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("logoUrl", logoUrl);
        docRef.update(updates).get();

        return logoUrl;
    }
}