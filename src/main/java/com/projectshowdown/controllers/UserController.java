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

/**
 * Controller for managing user-related operations.
 * Provides endpoints for CRUD operations on users and organizers, as well as bulk import functionality.
 */
@RestController
public class UserController {
    public static final String PLAYER_ROLE = "player";
    public static final String ORGANIZER_ROLE = "organizer";
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Constructs the controller with a {@link UserService}.
     *
     * @param userService The service handling user-related business logic.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves all players.
     *
     * @return A list of {@link UserDTO} objects representing players.
     * @throws ExecutionException   If an error occurs during the asynchronous operation.
     * @throws InterruptedException If the operation is interrupted.
     */
    @GetMapping("/users")
    public List<UserDTO> getPlayers() throws ExecutionException, InterruptedException {
        return userService.getAllUsersByRole(PLAYER_ROLE);
    }

    /**
     * Retrieves all organizers.
     *
     * @return A list of {@link UserDTO} objects representing organizers.
     * @throws ExecutionException   If an error occurs during the asynchronous operation.
     * @throws InterruptedException If the operation is interrupted.
     */
    @GetMapping("/organizers")
    public List<UserDTO> getOrganizers() throws ExecutionException, InterruptedException {
        return userService.getAllUsersByRole(ORGANIZER_ROLE);
    }

    /**
     * Retrieves details of a specific user.
     *
     * @param id The ID of the user to retrieve.
     * @return The {@link UserDTO} object representing the user.
     * @throws ExecutionException   If an error occurs during the asynchronous operation.
     * @throws InterruptedException If the operation is interrupted.
     * @throws PlayerNotFoundException If the user with the specified ID is not found.
     */
    @GetMapping("/user/{id}")
    public UserDTO getUser(@PathVariable String id) throws ExecutionException, InterruptedException {
        UserDTO player = userService.getUser(id);

        // Need to handle "player not found" error using proper HTTP status code
        // In this case it should be HTTP 404
        if (player == null)
            throw new PlayerNotFoundException(id);

        return player;
    }

    /**
     * Creates a new user.
     *
     * @param playerData A {@link User} object containing the user's details.
     * @return A success message with the user's ID.
     * @throws ExecutionException   If an error occurs during the asynchronous operation.
     * @throws InterruptedException If the operation is interrupted.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users")
    public String createUser(@Valid @RequestBody User playerData) throws ExecutionException, InterruptedException {
        // return playerData.getPlayerDetails().getName().toString();

        // Encode the password before storing it
        playerData.setPassword(passwordEncoder.encode(playerData.getPassword()));

        return userService.createUser(playerData);
    }

    /**
     * Updates a user's details.
     *
     * @param id       The ID of the user to update.
     * @param userData A map containing the fields to update and their new values.
     * @return A success message with the user's ID.
     * @throws ExecutionException   If an error occurs during the asynchronous operation.
     * @throws InterruptedException If the operation is interrupted.
     */
    @PutMapping("/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String updateUser(@PathVariable String id, @RequestBody Map<String, Object> userData)
            throws ExecutionException, InterruptedException {
        return userService.updateUser(id, userData);
    }

    /**
     * Verifies an organizer's account.
     *
     * @param id The ID of the organizer to verify.
     * @return A success message with the organizer's ID.
     * @throws ExecutionException   If an error occurs during the asynchronous operation.
     * @throws InterruptedException If the operation is interrupted.
     */
    @PutMapping("/organizer/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String verifyOrganizer(@PathVariable String id)
            throws ExecutionException, InterruptedException {
        return userService.verifyOrganizer(id);
    }

    /**
     * Deletes a user.
     *
     * @param id The ID of the user to delete.
     * @return A success message with the user's ID.
     * @throws ExecutionException   If an error occurs during the asynchronous operation.
     * @throws InterruptedException If the operation is interrupted.
     */
    @DeleteMapping("/user/{id}")
    public String deletePlayer(@PathVariable String id) throws ExecutionException, InterruptedException {
        return userService.deletePlayer(id);
    }


    /**
     * Performs a bulk import of users.
     *
     * @return A success message indicating the import operation is complete.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/import")
    public String massImport() {
        return userService.massImport();
    }

}