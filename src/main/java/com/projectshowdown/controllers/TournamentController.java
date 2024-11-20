package com.projectshowdown.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.projectshowdown.entities.Tournament;
import com.projectshowdown.service.TournamentService;

import jakarta.validation.Valid;

import java.util.concurrent.ExecutionException;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

/**
 * Controller for managing tournament-related operations.
 * Exposes endpoints to perform CRUD operations, handle user registrations, and upload tournament logos.
 */
@RestController
public class TournamentController {
    public Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    @Autowired
    TournamentService tournamentService;

    /**
     * Retrieves all tournaments.
     *
     * @return A list of {@link Tournament} objects.
     * @throws ExecutionException   If an error occurs while fetching tournaments.
     * @throws InterruptedException If the operation is interrupted.
     */
    @GetMapping("/tournaments")
    public List<Tournament> getTournaments() throws ExecutionException, InterruptedException {
        return tournamentService.getAllTournaments();
    }

    /**
     * Creates a new tournament with form data.
     *
     * @param name        The name of the tournament.
     * @param dateTime    The date and time of the tournament.
     * @param type        The type of the tournament.
     * @param year        The year of the tournament.
     * @param numPlayers  The number of players participating.
     * @param minMMR      The minimum MMR required to participate.
     * @param maxMMR      The maximum MMR allowed.
     * @param country     The country hosting the tournament.
     * @param venue       The venue of the tournament.
     * @param status      The status of the tournament (e.g., "Scheduled").
     * @param organizerId The organizer's ID.
     * @param file        The tournament logo file.
     * @return A success message with the generated tournament ID.
     * @throws ExecutionException   If an error occurs while creating the tournament.
     * @throws InterruptedException If the operation is interrupted.
     * @throws IOException          If an error occurs while uploading the logo.
     */
    @PostMapping("/tournaments")
    @ResponseStatus(HttpStatus.CREATED)
    public String addTournamentWithParams(
            @RequestParam("name") String name,
            @RequestParam("date") String dateTime,
            @RequestParam("type") String type,
            @RequestParam int year,
            @RequestParam("numPlayers") int numPlayers,
            @RequestParam("minMMR") double minMMR,
            @RequestParam("maxMMR") double maxMMR,
            @RequestParam("country") String country,
            @RequestParam("venue") String venue,
            @RequestParam("status") String status,
            @RequestParam("organizerId") String organizerId,
            @RequestParam("logo") MultipartFile file) throws ExecutionException, InterruptedException, IOException {

        Tournament tournament = new Tournament("", name, year, venue, country, dateTime, numPlayers, status, minMMR,
                maxMMR, "", null, organizerId, null);

        String generatedId = tournamentService.addTournament(tournament, organizerId);
        if (generatedId == null) {
            throw new RuntimeException("Failed to create tournament");
        }

        tournamentService.uploadLogoToFirebase(generatedId, file);
        return "Tournament created successfully with ID: " + generatedId;
    }

    /**
     * Retrieves tournaments by organizer ID.
     *
     * @param organizerId The organizer's ID.
     * @return A list of {@link Tournament} objects for the specified organizer.
     * @throws ExecutionException   If an error occurs while fetching tournaments.
     * @throws InterruptedException If the operation is interrupted.
     */
    @GetMapping("/tournaments/organizer/{organizerId}")
    public List<Tournament> getTournamentsByOrganizerId(@PathVariable String organizerId)
            throws ExecutionException, InterruptedException {
        return tournamentService.getTournamentsByOrganizerId(organizerId);
    }

    /**
     * Retrieves tournaments by player ID.
     *
     * @param playerId The player's ID.
     * @return A list of {@link Tournament} objects for the specified player.
     * @throws ExecutionException   If an error occurs while fetching tournaments.
     * @throws InterruptedException If the operation is interrupted.
     */
    @GetMapping("/tournaments/player/{playerId}")
    public List<Tournament> getTournamentsByPlayerId(@PathVariable String playerId)
            throws ExecutionException, InterruptedException {
        return tournamentService.getTournamentsByPlayerId(playerId);
    }

    /**
     * Displays details of a specific tournament.
     *
     * @param id The tournament ID.
     * @return A map containing the tournament details.
     * @throws ExecutionException   If an error occurs while fetching the tournament.
     * @throws InterruptedException If the tournament is not found.
     */
    @GetMapping("/tournament/{id}")
    public Map<String, Object> displayTournament(@PathVariable String id)
            throws ExecutionException, InterruptedException {
        Map<String, Object> tournament = tournamentService.displayTournament(id);

        // Need to handle "player not found" error using proper HTTP status code
        // In this case it should be HTTP 404
        if (tournament == null)
            throw new InterruptedException("Tournamt " + id + " not found");

        return tournament;
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/tournaments/{organizerId}")
    public String addTournament(
            @Valid @RequestParam String name,
            @RequestParam String dateTime,
            @RequestParam int numPlayers,
            @RequestParam int year,
            @RequestParam double minMMR,
            @RequestParam double maxMMR,
            @RequestParam String country,
            @RequestParam String venue,
            @RequestParam String status,
            @RequestParam("logo") MultipartFile file,
            @PathVariable String organizerId)
            throws ExecutionException, InterruptedException, IOException {

        Tournament tournament = new Tournament("", name, year, venue, country, dateTime, numPlayers, status, minMMR,
                maxMMR, "", null, organizerId, null);

        String generatedId = tournamentService.addTournament(tournament, organizerId);
        if (generatedId == null) {
            throw new RuntimeException("Failed to create tournament");
        }

        tournamentService.uploadLogoToFirebase(generatedId, file);
        return "Tournament created successfully with ID: " + generatedId;
    }

    /**
     * Updates a tournament's details.
     *
     * @param id           The tournament ID.
     * @param organizerId  The organizer's ID.
     * @param tournamentData A map containing the tournament's updated details.
     * @return A success message indicating the update status.
     * @throws ExecutionException   If an error occurs while updating the tournament.
     * @throws InterruptedException If the operation is interrupted.
     */
    @PutMapping("/tournament/{id}/{organizerId}")
    @ResponseStatus(HttpStatus.OK)
    public String updateTournament(@PathVariable String id, @PathVariable String organizerId,
            @RequestBody Map<String, Object> tournamentData)
            throws ExecutionException, InterruptedException {
        return tournamentService.updateTournament(id, organizerId, tournamentData);
    }

    /**
     * Registers a user for a tournament.
     *
     * @param id     The tournament ID.
     * @param userId The user ID.
     * @return A success message indicating the registration status.
     * @throws ExecutionException   If an error occurs during registration.
     * @throws InterruptedException If the operation is interrupted.
     */
    @PutMapping("/tournament/{id}/register/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public String registerUser(@PathVariable String id, @PathVariable String userId)
            throws ExecutionException, InterruptedException {
        return tournamentService.registerUser(id, userId);
    }

    /**
     * Cancels a user's registration for a tournament.
     *
     * @param id     The tournament ID.
     * @param userId The user ID.
     * @return A success message indicating the cancellation status.
     * @throws ExecutionException   If an error occurs during cancellation.
     * @throws InterruptedException If the operation is interrupted.
     */
    @PutMapping("/tournament/{id}/cancelRegistration/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public String cancelRegistration(@PathVariable String id, @PathVariable String userId)
            throws ExecutionException, InterruptedException {
        return tournamentService.cancelRegistration(id, userId);
    }

    /**
     * Progresses a tournament to the next stage.
     *
     * @param tournamentId The tournament ID.
     * @return A success message indicating the progression status.
     * @throws ExecutionException   If an error occurs while progressing the tournament.
     * @throws InterruptedException If the operation is interrupted.
     */
    @PutMapping("/tournament/{tournamentId}/matches")
    @ResponseStatus(HttpStatus.OK)
    public String progressTournament(@PathVariable String tournamentId)
            throws ExecutionException, InterruptedException {
        return tournamentService.progressTournament(tournamentId);
    }

    /**
     * Uploads a logo for a tournament.
     *
     * @param id   The tournament ID.
     * @param file The logo file to be uploaded.
     * @return A success message if the upload is successful.
     */
    @PostMapping("/tournament/{id}/uploadLogo")
    public ResponseEntity<String> uploadLogo(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        try {
            // Upload file and get URL
            tournamentService.uploadLogoToFirebase(id, file);
            // Update tournament with the logo URL
            // tournamentService.updateTournamentLogoUrl(id, logoUrl);
            return ResponseEntity.ok("Logo uploaded successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload logo: " + e.getMessage());
        }
    }
}
