package com.projectshowdown.controllers;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.projectshowdown.service.MatchService;

/**
 * Controller for managing match-related operations.
 * Exposes endpoints for updating match details.
 */
@RestController
public class MatchController {

    @Autowired
    MatchService matchService;

    /**
     * Updates the details of a specific match.
     *
     * @param id        The unique identifier of the match to update.
     * @param matchData A {@link Map} containing the updated match details.
     *                  The keys represent field names, and the values represent new field values.
     * @return A {@link String} message indicating the status of the update operation.
     * @throws ExecutionException   If an error occurs during the asynchronous operation with the database.
     * @throws InterruptedException If the operation is interrupted.
     */
    @PutMapping("/match/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String updateTournament(@PathVariable String id, @RequestBody Map<String, Object> matchData)
            throws ExecutionException, InterruptedException {
        return matchService.updateMatch(id, matchData);
    }
}
