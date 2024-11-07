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

@RestController
public class MatchController {

    @Autowired
    MatchService matchService;

    @PutMapping("/match/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String updateTournament(@PathVariable String id, @RequestBody Map<String, Object> matchData)
            throws ExecutionException, InterruptedException {
        return matchService.updateMatch(id, matchData);
    }
}
