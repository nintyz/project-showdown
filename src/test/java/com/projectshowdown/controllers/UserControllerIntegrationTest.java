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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
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
    private final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjpbImFkbWluIl0sInN1YiI6ImFhcm9uaml0ZWNrQGdtYWlsLmNvbSIsImlhdCI6MTcyOTE4NDMxMiwiZXhwIjoxNzI5MjIwMzEyfQ.pDOwOlQr-jVV2uDN2OymYVudItc1Emn4H0TKr7EWstU";

    @BeforeEach
    void setUp() throws Exception {
        baseUrl = "http://localhost:" + port;
        Player playerDetails = new Player(1, "Test Player", "2000-01-01", 24, 2000.0, 2500.0, 500.0, 400.0, 300.0, "", "", "");
        testUserDTO = new UserDTO(null, "test" + System.currentTimeMillis() + "@example.com" , "Password1@", "player", null, playerDetails);
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
    void testGetNonExistentPlayer() {
        ResponseEntity<UserDTO> getResponse = restTemplate.exchange(
                baseUrl + "/user/nonExistentId",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserDTO.class
        );

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void testUpdatePlayer() throws Exception {
        // First, add a player
        HttpEntity<UserDTO> addRequest = new HttpEntity<>(testUserDTO, headers);
        ResponseEntity<String> addResponse = restTemplate.postForEntity(baseUrl + "/users", addRequest, String.class);
        System.out.println(addResponse.getBody());
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
    void testUpdateNonExistentPlayer() throws Exception {
        UserDTO updatedUser = new UserDTO(null, "updated@example.com", "NewPassword1@", "player", null, null);
        HttpEntity<UserDTO> updateRequest = new HttpEntity<>(updatedUser, headers);
        ResponseEntity<String> updateResponse = restTemplate.exchange(
                baseUrl + "/user/nonExistentId",
                HttpMethod.PUT,
                updateRequest,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, updateResponse.getStatusCode());
    }

    @Test
    void testDeletePlayer() throws Exception {
        // First, add a player
        HttpEntity<UserDTO> addRequest = new HttpEntity<>(testUserDTO, headers);
        ResponseEntity<String> addResponse = restTemplate.postForEntity(baseUrl + "/users", addRequest, String.class);
        System.out.println(addResponse.getBody());
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

    @Test
    void testDeleteNonExistentPlayer() throws Exception {
        ResponseEntity<String> deleteResponse = restTemplate.exchange(
                baseUrl + "/user/nonExistentId",
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, deleteResponse.getStatusCode());
    }

    @Test
    void testAddPlayerWithInvalidData() throws Exception {
        // Create invalid player data (missing required fields)
        testUserDTO.setEmail(null);

        HttpEntity<UserDTO> request = new HttpEntity<>(testUserDTO, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/users", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testGetAllPlayers() throws Exception {
        // Add a few test players
        for (int i = 0; i < 3; i++) {
            testUserDTO.setEmail("test" + i + System.currentTimeMillis() + "@example.com");
            HttpEntity<UserDTO> request = new HttpEntity<>(testUserDTO, headers);
            restTemplate.postForEntity(baseUrl + "/users", request, String.class);
        }

        // Get all players
        ResponseEntity<List<UserDTO>> response = restTemplate.exchange(
                baseUrl + "/users",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<UserDTO>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    private String extractUserId(String response) {
        int start = response.indexOf("ID: ") + 4;
        int end = response.indexOf(" at:");
        return response.substring(start, end);
    }
}