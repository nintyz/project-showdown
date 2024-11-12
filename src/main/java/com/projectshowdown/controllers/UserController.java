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
import java.util.Map;

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

    // GET all verified organizers
    @GetMapping("/organizers")
    public List<UserDTO> getOrganizers() throws ExecutionException, InterruptedException {
        return userService.getAllOrganizers();
    }

    // GET all pending organizers
    @GetMapping("/pending-organizers")
    public List<UserDTO> getPendingOrganizers() throws ExecutionException, InterruptedException {
        return userService.getAllPendingOrganizers();
    }

    // get specific player
    @GetMapping("/user/{id}")
    public UserDTO getUser(@PathVariable String id) throws ExecutionException, InterruptedException {
        UserDTO player = userService.getUser(id);

        // Need to handle "player not found" error using proper HTTP status code
        // In this case it should be HTTP 404
        if (player == null)
            throw new PlayerNotFoundException(id);

        return player;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users")
    public String createUser(@Valid @RequestBody User playerData) throws ExecutionException, InterruptedException {
        // return playerData.getPlayerDetails().getName().toString();

        // Encode the password before storing it
        playerData.setPassword(passwordEncoder.encode(playerData.getPassword()));

        return userService.createUser(playerData);
    }

    @PutMapping("/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String updateUser(@PathVariable String id, @RequestBody Map<String, Object> userData)
            throws ExecutionException, InterruptedException {
        return userService.updateUser(id, userData);
    }

    @PutMapping("/organizer/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String verifyOrganizer(@PathVariable String id)
            throws ExecutionException, InterruptedException {
        return userService.verifyOrganizer(id);
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