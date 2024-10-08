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

import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.exceptions.PlayerNotFoundException;
import com.projectshowdown.service.CustomUserDetailsService;
import com.projectshowdown.user.User;

import jakarta.validation.Valid;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.List;

@RestController
public class UserController {
    @Autowired
    private CustomUserDetailsService userService;

    public UserController(CustomUserDetailsService userService) {
        this.userService = userService;
    }

    // GET all players
    @GetMapping("/users")
    public List<Map<String, Object>> getPlayers() throws ExecutionException, InterruptedException {
        return userService.getAllPlayers();
    }

   
    //get specific player
    @GetMapping("/user/{id}")
    public UserDTO getPlayer(@PathVariable int id) throws ExecutionException, InterruptedException {
        UserDTO player = userService.getPlayer(id);

        // Need to handle "player not found" error using proper HTTP status code
        // In this case it should be HTTP 404
        if (player == null)
            throw new PlayerNotFoundException(id);
            
        return player;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users")
    public String addPlayer(@Valid @RequestBody User playerData) throws ExecutionException, InterruptedException {
        //return playerData.getPlayerDetails().getName().toString();
        return userService.addPlayer(playerData);
    }

    @PutMapping("/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String updatePlayer(@PathVariable String id, @RequestBody User playerData) throws ExecutionException, InterruptedException {
        return userService.updatePlayer(id, playerData);
    }

    @DeleteMapping("/user/{id}")
    public String deletePlayer(@PathVariable int id) throws ExecutionException, InterruptedException {
        return userService.deletePlayer(id);
    }

}