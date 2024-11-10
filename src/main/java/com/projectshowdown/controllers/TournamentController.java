package com.projectshowdown.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import com.projectshowdown.entities.Tournament;
import com.projectshowdown.service.TournamentService;

import jakarta.validation.Valid;

import java.util.concurrent.ExecutionException;
import java.util.List;
import java.util.Map;

@RestController
public class TournamentController {
    @Autowired
    TournamentService tournamentService;

    // GET all players
    @GetMapping("/tournaments")
    public List<Tournament> getTournaments() throws ExecutionException, InterruptedException {
        return tournamentService.getAllTournaments();
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
    public String addTournament(@Valid @RequestBody Tournament tournamentData, @PathVariable String organizerId)
            throws ExecutionException, InterruptedException {
        return tournamentService.addTournament(tournamentData, organizerId);
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
}
