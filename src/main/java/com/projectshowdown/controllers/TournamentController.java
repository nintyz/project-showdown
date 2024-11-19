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

@RestController
public class TournamentController {
    public Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    @Autowired
    TournamentService tournamentService;

    // GET all players
    @GetMapping("/tournaments")
    public List<Tournament> getTournaments() throws ExecutionException, InterruptedException {
        return tournamentService.getAllTournaments();
    }

    // POST - add tournament with form data
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

    @GetMapping("/tournaments/organizer/{organizerId}")
    public List<Tournament> getTournamentsByOrganizerId(@PathVariable String organizerId)
            throws ExecutionException, InterruptedException {
        return tournamentService.getTournamentsByOrganizerId(organizerId);
    }

    @GetMapping("/tournaments/player/{playerId}")
    public List<Tournament> getTournamentsByPlayerId(@PathVariable String playerId)
            throws ExecutionException, InterruptedException {
        return tournamentService.getTournamentsByPlayerId(playerId);
    }

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

    @PutMapping("/tournament/{id}/{organizerId}")
    @ResponseStatus(HttpStatus.OK)
    public String updateTournament(@PathVariable String id, @PathVariable String organizerId,
            @RequestBody Map<String, Object> tournamentData)
            throws ExecutionException, InterruptedException {
        return tournamentService.updateTournament(id, organizerId, tournamentData);
    }

    @PutMapping("/tournament/{id}/register/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public String registerUser(@PathVariable String id, @PathVariable String userId)
            throws ExecutionException, InterruptedException {
        return tournamentService.registerUser(id, userId);
    }

    @PutMapping("/tournament/{id}/cancelRegistration/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public String cancelRegistration(@PathVariable String id, @PathVariable String userId)
            throws ExecutionException, InterruptedException {
        return tournamentService.cancelRegistration(id, userId);
    }

    @PutMapping("/tournament/{tournamentId}/matches")
    @ResponseStatus(HttpStatus.OK)
    public String progressTournament(@PathVariable String tournamentId)
            throws ExecutionException, InterruptedException {
        return tournamentService.progressTournament(tournamentId);
    }

    // upload logo to firebase storage
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
