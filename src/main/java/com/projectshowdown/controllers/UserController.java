package com.projectshowdown.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.entities.User;
import com.projectshowdown.exceptions.PlayerNotFoundException;
import com.projectshowdown.service.UserService;

import jakarta.validation.Valid;

import java.util.concurrent.ExecutionException;
import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET all players
    @GetMapping("/users")
    public List<UserDTO> getPlayers() throws ExecutionException, InterruptedException {
        return userService.getAllPlayers();
    }

    // get specific player
    @GetMapping("/user/{id}")
    public UserDTO getPlayer(@PathVariable String id) throws ExecutionException, InterruptedException {
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
        // return playerData.getPlayerDetails().getName().toString();

        // Encode the password before storing it
        playerData.setPassword(passwordEncoder.encode(playerData.getPassword()));

        return userService.addPlayer(playerData);
    }

    @PutMapping("/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String updatePlayer(@PathVariable String id, @RequestBody User playerData)
            throws ExecutionException, InterruptedException {
        playerData.setId(id);
        return userService.updatePlayer(id, playerData);
    }

    @DeleteMapping("/user/{id}")
    public String deletePlayer(@PathVariable String id) throws ExecutionException, InterruptedException {
        return userService.deletePlayer(id);
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/import")
    public String massImport() {
        return userService.massImport();
    }

}