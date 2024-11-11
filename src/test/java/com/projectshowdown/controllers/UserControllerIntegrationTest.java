package com.projectshowdown.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectshowdown.config.TestSecurityConfig;
import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.entities.Organizer;
import com.projectshowdown.entities.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class)
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

    private List<String> createdUserIds = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port;
        Player playerDetails = new Player(1, "Test Player", "2000-01-01", 24, 2000.0, 2500.0, 500.0, 400.0, 300.0, "", "", "");
        Organizer organizerDetails = new Organizer("Test Organizer", true, "2000-01-01", "", "Singpore", "test.com");
        testUserDTO = new UserDTO(null, "test" + System.currentTimeMillis() + "@example.com" , "Password1@", "player", null, playerDetails, organizerDetails, null, null, true);
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @AfterEach
    void tearDown() {
        // Delete all users created during the test
        for (String userId : createdUserIds) {
            try {
                restTemplate.exchange(
                        baseUrl + "/user/" + userId,
                        HttpMethod.DELETE,
                        new HttpEntity<>(headers),
                        String.class
                );
            } catch (Exception e) {
                // Ignore errors during cleanup
                System.out.println("Could not delete user " + userId + ": " + e.getMessage());
            }
        }
        createdUserIds.clear();
    }

    @Test
    void testAddPlayer() {
        HttpEntity<UserDTO> request = new HttpEntity<>(testUserDTO, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/users", request, String.class);
        extractUserId(response.getBody());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("User created successfully with ID:"));
    }

    @Test
    void testGetPlayer() throws Exception {
        // First, add a player
        HttpEntity<UserDTO> addRequest = new HttpEntity<>(testUserDTO, headers);
        ResponseEntity<String> addResponse = restTemplate.postForEntity(baseUrl + "/users", addRequest, String.class);
        String userId = extractUserId(addResponse.getBody());

        // Then, get the player
        ResponseEntity<String> getResponse = restTemplate.exchange(
                baseUrl + "/user/" + userId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());

        // Parse only the fields we need using JsonNode
        JsonNode jsonNode = objectMapper.readTree(getResponse.getBody());
        assertEquals(testUserDTO.getEmail(), jsonNode.get("email").asText());
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
        String userId = extractUserId(addResponse.getBody());

        // Update the player
        testUserDTO.getPlayerDetails().setName("Updated Name");
        HttpEntity<UserDTO> updateRequest = new HttpEntity<>(testUserDTO, headers);
        ResponseEntity<String> updateResponse = restTemplate.exchange(
                baseUrl + "/user/" + userId,
                HttpMethod.PUT,
                updateRequest,
                String.class  // Changed from UserDTO.class to String.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());

        // Verify the update
        ResponseEntity<String> getResponse = restTemplate.exchange(
                baseUrl + "/user/" + userId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class  // Changed from UserDTO.class to String.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        JsonNode jsonNode = objectMapper.readTree(getResponse.getBody());
        assertEquals("Updated Name", jsonNode.get("playerDetails").get("name").asText());
    }

    @Test
    void testUpdateNonExistentPlayer() {
        UserDTO updatedUser = new UserDTO(null, "test" + System.currentTimeMillis() + "@example.com" , "Password1@", "player", null, null, null, null, null, true);
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
    void testDeletePlayer() {
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

    @Test
    void testDeleteNonExistentPlayer() {
        ResponseEntity<String> deleteResponse = restTemplate.exchange(
                baseUrl + "/user/nonExistentId",
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, deleteResponse.getStatusCode());
    }

    @Test
    void testAddPlayerWithInvalidData() {
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
            ResponseEntity<String> addResponse = restTemplate.postForEntity(baseUrl + "/users", request, String.class);
            extractUserId(addResponse.getBody());
        }

        // Get all players
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/users",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        // Parse response as JsonNode array
        JsonNode jsonArray = objectMapper.readTree(response.getBody());
        assertTrue(jsonArray.isArray());
        assertFalse(jsonArray.isEmpty());
    }

    private String extractUserId(String response) {
        int start = response.indexOf("ID: ") + 4;
        int end = response.indexOf(" at:");
        String userId = response.substring(start, end);
        createdUserIds.add(userId); // Track the created user ID
        return userId;
    }
}