package com.projectshowdown.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.entities.Tournament;
import com.projectshowdown.exceptions.PlayerNotFoundException;
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

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/tournaments")
    public String addPlayer(@Valid @RequestBody Tournament tournamentData)
            throws ExecutionException, InterruptedException {
        return tournamentService.addTournament(tournamentData);
    }

    @GetMapping("/tournament/{id}")
    public Tournament getPlayer(@PathVariable String id) throws ExecutionException, InterruptedException {
        Tournament tournament = tournamentService.getTournament(id);

        // Need to handle "player not found" error using proper HTTP status code
        // In this case it should be HTTP 404
        if (tournament == null)
            throw new InterruptedException("Tournamt " + id + " not found");

        return tournament;
    }

    @PutMapping("/tournament/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String updateTournament(@PathVariable String id, @RequestBody Map<String, Object> tournamentData)
            throws ExecutionException, InterruptedException {
        return tournamentService.updateTournament(id, tournamentData);
    }

    @PutMapping("/tournament/{id}/register/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public String registerUser(@PathVariable String id, @PathVariable String userId)
            throws ExecutionException, InterruptedException {
        return tournamentService.registerUser(id, userId);
    }
}
