package com.projectshowdown.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.entities.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;
    private UserDTO testUserDTO;
    private HttpHeaders headers;
    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        baseUrl = "http://localhost:" + port;
        Player playerDetails = new Player(1, "Test Player", LocalDate.now(), 24, 2000.0, 2500.0, 500.0, 400.0, 300.0);
        testUserDTO = new UserDTO(null, "test@example.com", "Password1@", "player", null, playerDetails);

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Get authentication token
        authToken = getAuthToken();
        headers.set("Authorization", "Bearer " + authToken);
    }

    private String getAuthToken() throws Exception {
        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> loginRequest = Map.of(
                "email", "aaronjiteck@gmail.com",
                "password", "password"
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(loginRequest, loginHeaders);
        ResponseEntity<Map> loginResponse = restTemplate.postForEntity(baseUrl + "/login", request, Map.class);

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());
        return (String) loginResponse.getBody().get("token");
    }

    @Test
    void testAddPlayer() throws Exception {
        HttpEntity<UserDTO> request = new HttpEntity<>(testUserDTO, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/users", request, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("Player created successfully with ID:"));
    }

    @Test
    void testGetPlayer() throws Exception {
        // First, add a player
        HttpEntity<UserDTO> addRequest = new HttpEntity<>(testUserDTO, headers);
        ResponseEntity<String> addResponse = restTemplate.postForEntity(baseUrl + "/users", addRequest, String.class);
        String userId = extractUserId(addResponse.getBody());

        // Then, get the player
        ResponseEntity<UserDTO> getResponse = restTemplate.exchange(
                baseUrl + "/user/" + userId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserDTO.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(testUserDTO.getEmail(), getResponse.getBody().getEmail());
    }

    @Test
    void testUpdatePlayer() throws Exception {
        // First, add a player
        HttpEntity<UserDTO> addRequest = new HttpEntity<>(testUserDTO, headers);
        ResponseEntity<String> addResponse = restTemplate.postForEntity(baseUrl + "/users", addRequest, String.class);
        String userId = extractUserId(addResponse.getBody());

        // Update the player
        testUserDTO.getPlayerDetails().setName("Updated Name");
        HttpEntity<UserDTO> updateRequest = new HttpEntity<>(testUserDTO, headers);
        ResponseEntity<String> updateResponse = restTemplate.exchange(
                baseUrl + "/user/" + userId,
                HttpMethod.PUT,
                updateRequest,
                String.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertTrue(updateResponse.getBody().contains("User with ID: " + userId + " updated successfully"));

        // Verify the update
        ResponseEntity<UserDTO> getResponse = restTemplate.exchange(
                baseUrl + "/user/" + userId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserDTO.class
        );
        assertEquals("Updated Name", getResponse.getBody().getPlayerDetails().getName());
    }

    @Test
    void testDeletePlayer() throws Exception {
        // First, add a player
        HttpEntity<UserDTO> addRequest = new HttpEntity<>(testUserDTO, headers);
        ResponseEntity<String> addResponse = restTemplate.postForEntity(baseUrl + "/users", addRequest, String.class);
        String userId = extractUserId(addResponse.getBody());

        // Delete the player
        ResponseEntity<String> deleteResponse = restTemplate.exchange(
                baseUrl + "/user/" + userId,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                String.class
        );

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertTrue(deleteResponse.getBody().contains("Player with the id:" + userId + " successfully deleted"));

        // Verify the deletion
        ResponseEntity<UserDTO> getResponse = restTemplate.exchange(
                baseUrl + "/user/" + userId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserDTO.class
        );
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    private String extractUserId(String response) {
        // Extract the user ID from the response string
        // This is a simple implementation and might need to be adjusted based on the actual response format
        int start = response.indexOf("ID: ") + 4;
        int end = response.indexOf(" at:");
        return response.substring(start, end);
    }
}