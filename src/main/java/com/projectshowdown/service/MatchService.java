package com.projectshowdown.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.entities.Match;
import com.projectshowdown.events.MatchUpdatedEvent;

import jakarta.mail.MessagingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class MatchService {

    public static final String MATCHES_DB = "matches";
    public static final String TOURNAMENT_ID_FIELD = "tournamentId";
    public static final String DATE_TIME_FIELD = "dateTime";
    public static final String PLAYER_1_SCORE_FIELD = "player1Score";
    public static final String PLAYER_2_SCORE_FIELD = "player2Score";
    public static final String TOURNAMENTS_DB = "tournaments";
    public static final String NAME_FIELD = "name";
    public static final String PLAYER_1_ID_FIELD = "player1Id";
    public static final String PLAYER_2_ID_FIELD = "player2Id";
    @Autowired
    UserService userService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private static final int ELO_GAINED_WHEN_YOU_WIN = 25;

    /**
     * Retrieves the Firestore database instance.
     *
     * @return The Firestore instance.
     */
    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    /**
     * Adds a new match to Firestore.
     *
     * @param matchToSave The Match object to save.
     * @return The ID of the saved match.
     * @throws ExecutionException   If an error occurs during the Firestore
     *                              operation.
     * @throws InterruptedException If the operation is interrupted.
     */
    public String addMatch(Match matchToSave) throws ExecutionException, InterruptedException {
        Firestore db = getFirestore();
        DocumentReference docRef = db.collection(MATCHES_DB).document(matchToSave.getId());
        docRef.set(matchToSave).get();
        return matchToSave.getId();
    }

    /**
     * Updates a match in Firestore with new data and handles associated logic
     * such as notifying players, updating scores, and publishing events.
     *
     * @param id        The ID of the match to update.
     * @param matchData A map of the fields to update.
     * @return A success message with the update time or an error message.
     * @throws ExecutionException   If an error occurs during the Firestore
     *                              operation.
     * @throws InterruptedException If the operation is interrupted.
     */
    public String updateMatch(String id, Map<String, Object> matchData)
            throws ExecutionException, InterruptedException {
        Firestore db = getFirestore();
        DocumentReference docRef = db.collection(MATCHES_DB).document(id);

        // Check if the match exists
        DocumentSnapshot document = docRef.get().get();
        if (!document.exists()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Unable to find match with id: " + id);
        }

        String tournamentId = document.getString(TOURNAMENT_ID_FIELD);

        // Ensure the match has a date and time set if updating scores
        if ("TBC".equals(document.getString(DATE_TIME_FIELD)) && !matchData.containsKey(DATE_TIME_FIELD)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Please update match's date and time details before attempting to update the scores.");
        }

        // Notify players if the match date and time are updated
        if (matchData.containsKey(DATE_TIME_FIELD)) {
            notifyPlayersAboutMatchUpdate(document, matchData.get(DATE_TIME_FIELD).toString(), tournamentId);
        }

        // Mark match as completed if both player scores are updated
        if (matchData.containsKey(PLAYER_1_SCORE_FIELD) && matchData.containsKey(PLAYER_2_SCORE_FIELD)) {
            matchData.put("completed", true);
        }

        // Filter out null values from the match data
        Map<String, Object> filteredUpdates = matchData.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Update Firestore with the filtered match data
        WriteResult writeResult = docRef.update(filteredUpdates).get();

        // Publish match updated event
        Match match = document.toObject(Match.class);
        eventPublisher.publishEvent(new MatchUpdatedEvent(this, tournamentId, match));

        // Update ELO for the winner if scores are updated
        if (matchData.containsKey(PLAYER_1_SCORE_FIELD) && matchData.containsKey(PLAYER_2_SCORE_FIELD)) {
            updateWinnerElo(match);
        }

        return "Match with ID: " + id + " updated successfully at: " + writeResult.getUpdateTime();
    }

    /**
     * Notifies players about an updated match date and time via email.
     *
     * @param document     The match document containing player information.
     * @param newDateTime  The new date and time for the match.
     * @param tournamentId The ID of the tournament the match belongs to.
     * @throws ExecutionException   If an error occurs during the Firestore
     *                              operation.
     * @throws InterruptedException If the operation is interrupted.
     */
    private void notifyPlayersAboutMatchUpdate(DocumentSnapshot document, String newDateTime, String tournamentId)
            throws ExecutionException, InterruptedException {
        Firestore db = getFirestore();

        // Parse and format the date and time
        String[] dateTimeParts = newDateTime.split("T");
        String date = dateTimeParts[0];
        String time = dateTimeParts[1].substring(0, 5);
        String amPmTime = java.time.LocalTime.parse(time)
                .format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));

        // Retrieve tournament details
        DocumentSnapshot tournamentDoc = db.collection(TOURNAMENTS_DB).document(tournamentId).get().get();
        String tournamentName = tournamentDoc.exists() ? tournamentDoc.getString(NAME_FIELD) : "Unknown Tournament";

        // Retrieve player details
        UserDTO user1 = userService.getUser(document.getString(PLAYER_1_ID_FIELD));
        UserDTO user2 = userService.getUser(document.getString(PLAYER_2_ID_FIELD));

        // Send email notifications
        try {
            notificationService.notifyMatchDetailsUpdated(
                    user1.getEmail(), user1.getName(), user2.getName(), tournamentName, date, amPmTime);

            notificationService.notifyMatchDetailsUpdated(
                    user2.getEmail(), user2.getName(), user1.getName(), tournamentName, date, amPmTime);
        } catch (MessagingException e) {
            System.out.println("Failed to send updated match notification emails.");
            e.printStackTrace();
        }
    }

    /**
     * Updates the ELO score for the winner of a match.
     *
     * @param match The Match object containing the winner's information.
     * @throws ExecutionException   If an error occurs during the Firestore
     *                              operation.
     * @throws InterruptedException If the operation is interrupted.
     */
    private void updateWinnerElo(Match match) throws ExecutionException, InterruptedException {
        UserDTO winner = userService.getUser(match.winnerId());
        Map<String, Object> toUpdateScore = new HashMap<>();
        double newElo = winner.getPlayerDetails().getElo() + ELO_GAINED_WHEN_YOU_WIN;
        toUpdateScore.put("playerDetails.elo", newElo);

        // Update peak ELO if the new ELO exceeds the previous peak
        if (winner.getPlayerDetails().getPeakElo() < newElo) {
            toUpdateScore.put("playerDetails.peakElo", newElo);
        }

        userService.updateUser(winner.getId(), toUpdateScore);
    }

    /**
     * Checks if all matches in the current round are completed.
     *
     * @param currentRound A list of match IDs in the current round.
     * @return True if all matches are completed, false otherwise.
     * @throws ExecutionException   If an error occurs during the Firestore
     *                              operation.
     * @throws InterruptedException If the operation is interrupted.
     */
    public boolean checkCurrentRoundCompletion(List<String> currentRound)
            throws ExecutionException, InterruptedException {
        for (Match match : getMatches(currentRound)) {
            if (!match.isCompleted()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieves a match by its ID.
     *
     * @param matchId The ID of the match.
     * @return The Match object.
     * @throws ExecutionException   If an error occurs during the Firestore
     *                              operation.
     * @throws InterruptedException If the operation is interrupted.
     */
    public Match getMatch(String matchId) throws ExecutionException, InterruptedException {
        Firestore db = getFirestore();
        DocumentSnapshot document = db.collection(MATCHES_DB).document(matchId).get().get();

        if (document.exists()) {
            Match match = document.toObject(Match.class);
            match.setId(matchId);
            return match;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Unable to find match: " + matchId);
        }
    }

    /**
     * Retrieves multiple matches by their IDs.
     *
     * @param matchIds A list of match IDs.
     * @return A list of Match objects.
     * @throws ExecutionException   If an error occurs during the Firestore
     *                              operation.
     * @throws InterruptedException If the operation is interrupted.
     */
    public List<Match> getMatches(List<String> matchIds) throws ExecutionException, InterruptedException {
        List<Match> matches = new ArrayList<>();
        for (String matchId : matchIds) {
            matches.add(getMatch(matchId));
        }
        return matches;
    }
}
