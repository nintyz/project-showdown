package com.projectshowdown.controllers;

import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectshowdown.config.TestGoogleServiceConfig;
import com.projectshowdown.config.TestSecurityConfig;
import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.entities.Player;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "SUPPORT_EMAIL=admin@gmail.com")
@TestPropertySource(properties = "APP_PASSWORD=Password1!")
@TestPropertySource(properties = "GOOGLE_CONFIG_PATH=/Users/arthurchan/Documents/firebasekey/serviceAccountKey.json")
@TestPropertySource(properties = "GOOGLE_CREDENTIALS_JSON=/Users/arthurchan/Documents/firebasekey/serviceAccountKey.json")

@Import({TestGoogleServiceConfig.class, TestSecurityConfig.class})
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
        Player playerDetails = new Player(1, "2000-01-01", 2000.0, 24, 2500.0, "", "", "");
        this.testUserDTO = new UserDTO(
            "testUserId",
            "testName",
            "testProfileUrl",
                "test" + UUID.randomUUID().toString() + "@gmail.com",
            "Password1@",
            "player",
            "testTwoFactorSecret",
            playerDetails,
            null, // organizerDetails
            "testVerificationCode",
            null, // verificationCodeExpiresAt
            true // enabled
        );
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @AfterEach
    public void tearDown() {
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
        response.getBody();
        System.out.println(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(!response.getBody().contains("exists"));
    }

    @Test
    void testGetPlayer() throws Exception {
        // First, add a player
        HttpEntity<UserDTO> addRequest = new HttpEntity<>(testUserDTO, headers);
        ResponseEntity<String> addResponse = restTemplate.postForEntity(baseUrl + "/users", addRequest, String.class);
        String userId = addResponse.getBody();
        System.out.println(addResponse.getBody());
        sleep(50);
        // Then, get the player
        ResponseEntity<String> getResponse = restTemplate.exchange(
                baseUrl + "/user/" + userId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        System.out.println(getResponse.getBody());
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
    void testUpdateNonExistentPlayer() {
        UserDTO updatedUser = new UserDTO(
            "testUpdatedUserId",
            "testUpdatedUserName",
            "testProfileUrl",
            "testEmail",
            "Password1@",
            "player",
            "testTwoFactorSecret",
            null,
            null, // organizerDetails
            "testVerificationCode",
            null, // verificationCodeExpiresAt
            true // enabled
            );
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
    void testDeletePlayer() throws InterruptedException {
        // First, add a player
        HttpEntity<UserDTO> addRequest = new HttpEntity<>(testUserDTO, headers);
        ResponseEntity<String> addResponse = restTemplate.postForEntity(baseUrl + "/users", addRequest, String.class);
        String userId = addResponse.getBody();

        sleep(50);

        // Delete the player
        ResponseEntity<String> deleteResponse = restTemplate.exchange(
                baseUrl + "/user/" + userId,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                String.class
        );
        System.out.println(deleteResponse.getBody());
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
            addResponse.getBody();
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
}