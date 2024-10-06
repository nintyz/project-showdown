package com.projectshowdown.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.projectshowdown.exceptions.PlayerNotFoundException;
import com.projectshowdown.player.Player;
import com.projectshowdown.service.PlayerService;

import jakarta.validation.Valid;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.List;

@RestController
public class PlayerController {
    @Autowired
    private PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    // GET all players
    @GetMapping("/players")
    public List<Map<String, Object>> getPlayers() throws ExecutionException, InterruptedException {
        return playerService.getAllPlayers();
    }

   
    //get specific player
    @GetMapping("/player/{id}")
    public Player getPlayer(@PathVariable int id) throws ExecutionException, InterruptedException {
        Player player = playerService.getPlayer(id);

        // Need to handle "player not found" error using proper HTTP status code
        // In this case it should be HTTP 404
        if (player == null)
            throw new PlayerNotFoundException(id);
            
        return player;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/players")
    public String addPlayer(@Valid @RequestBody Player playerData) throws ExecutionException, InterruptedException {
        return playerService.addPlayer(playerData);
    }

    @PutMapping("/player/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String updatePlayer(@PathVariable String id, @RequestBody Player playerData) throws ExecutionException, InterruptedException {
        return playerService.updatePlayer(id, playerData);
    }

    @DeleteMapping("/player/{id}")
    public String deletePlayer(@PathVariable int id) throws ExecutionException, InterruptedException {
        return playerService.deletePlayer(id);
    }

}