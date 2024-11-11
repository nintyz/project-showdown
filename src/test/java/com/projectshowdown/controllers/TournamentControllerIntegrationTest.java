package com.projectshowdown.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectshowdown.config.TestSecurityConfig;
import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.entities.Player;
import com.projectshowdown.entities.Tournament;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
public class TournamentControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;
    private HttpHeaders headers;
    private Tournament testTournament;
    private List<String> createdTournamentIds;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        createdTournamentIds = new ArrayList<>();

        testTournament = new Tournament(
                "testId",
                "Test Tournament",
                2024,
                "Test Venue",
                "2024-12-31T10:15:30",
                32,
                "pending",
                1000.0,
                2000.0,
                new ArrayList<>(),  // Initialize empty Rounds List
                "testOrganizerId",
                new ArrayList<>()   // Initialize empty user List
        );
    }

    @Test
    void testAddTournament() {
        HttpEntity<Tournament> request = new HttpEntity<>(testTournament, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/tournaments",
                request,
                String.class
        );
        System.out.println(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("Tournament created successfully with ID:"));

        // Extract and store tournament ID for cleanup
        String tournamentId = extractTournamentId(response.getBody());
        createdTournamentIds.add(tournamentId);
    }

    @Test
    void testGetTournaments() {
        // First create a tournament
        HttpEntity<Tournament> createRequest = new HttpEntity<>(testTournament, headers);
        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                baseUrl + "/tournaments",
                createRequest,
                String.class
        );
        System.out.println(createResponse.getBody());
        createdTournamentIds.add(extractTournamentId(createResponse.getBody()));

        // Get all tournaments
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/tournaments",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testDisplayTournament() {
        // First create a tournament
        HttpEntity<Tournament> createRequest = new HttpEntity<>(testTournament, headers);
        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                baseUrl + "/tournaments",
                createRequest,
                String.class
        );
        String tournamentId = extractTournamentId(createResponse.getBody());

        // Get the tournament
        ResponseEntity<Map> response = restTemplate.getForEntity(
                baseUrl + "/tournament/" + tournamentId,
                Map.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Tournament", response.getBody().get("name"));
    }

    @Test
    void testUpdateTournament() {
        // First create a tournament
        HttpEntity<Tournament> createRequest = new HttpEntity<>(testTournament, headers);
        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                baseUrl + "/tournaments",
                createRequest,
                String.class
        );
        String tournamentId = extractTournamentId(createResponse.getBody());
        createdTournamentIds.add(tournamentId);

        // Update tournament data
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("name", "Updated Tournament Name");
        updateData.put("venue", "Updated Venue");
        updateData.put("year", 2024);
        updateData.put("format", "Single Elimination");
        updateData.put("date", "2024-12-31");
        updateData.put("maxPlayers", 32);
        updateData.put("status", "pending");
        updateData.put("minMMR", 1000.0);
        updateData.put("maxMMR", 2000.0);

        HttpEntity<Map<String, Object>> updateRequest = new HttpEntity<>(updateData, headers);
        ResponseEntity<String> updateResponse = restTemplate.exchange(
                baseUrl + "/tournament/" + tournamentId,
                HttpMethod.PUT,
                updateRequest,
                String.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
    }

@Test
void testCancelTournament() throws Exception {
        // First, create a tournament
        HttpEntity<Tournament> createRequest = new HttpEntity<>(testTournament, headers);
        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                baseUrl + "/tournaments",
                createRequest,
                String.class
        );
        String tournamentId = extractTournamentId(createResponse.getBody());
        createdTournamentIds.add(tournamentId); // Track it for cleanup

        // Prepare cancellation data
        Map<String, Object> cancelData = new HashMap<>();
        cancelData.put("status", "cancelled");

        // Send cancellation update request
        HttpEntity<Map<String, Object>> cancelRequest = new HttpEntity<>(cancelData, headers);
        ResponseEntity<String> cancelResponse = restTemplate.exchange(
                baseUrl + "/tournament/" + tournamentId,
                HttpMethod.PUT,
                cancelRequest,
                String.class
        );

        // Assertions to verify cancellation
        assertEquals(HttpStatus.OK, cancelResponse.getStatusCode());
        assertTrue(cancelResponse.getBody().contains("updated successfully"));
        
        // Fetch the tournament to verify its status is now "cancelled"
        ResponseEntity<Map> tournamentResponse = restTemplate.getForEntity(
                baseUrl + "/tournament/" + tournamentId,
                Map.class
        );

        assertEquals(HttpStatus.OK, tournamentResponse.getStatusCode());
        assertEquals("cancelled", tournamentResponse.getBody().get("status"));
}

    @Test
    void testRegisterUser() {
        // First create a test user
        Player playerDetails = new Player(1, "2000-01-01", 1500.0, 24, 2500.0, "", "", "");
        UserDTO testUser = new UserDTO(
                "testUserId",
                "test" + System.currentTimeMillis() + "@example.com",
                "Password1@",
                "player",
                null,
                playerDetails,
                null,
                null,
                null,
                true
        );

        HttpEntity<UserDTO> userRequest = new HttpEntity<>(testUser, headers);
        ResponseEntity<String> userResponse = restTemplate.postForEntity(
                baseUrl + "/users",
                userRequest,
                String.class
        );
        String userId = extractUserId(userResponse.getBody());

        // Then create a tournament
        HttpEntity<Tournament> createRequest = new HttpEntity<>(testTournament, headers);
        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                baseUrl + "/tournaments",
                createRequest,
                String.class
        );
        String tournamentId = extractTournamentId(createResponse.getBody());
        createdTournamentIds.add(tournamentId);

        // Debug prints
        System.out.println("User ID: " + userId);
        System.out.println("Tournament ID: " + tournamentId);
        System.out.println("Registration URL: " + baseUrl + "/tournament/" + tournamentId + "/register/" + userId);

        // Register the user
        ResponseEntity<String> registerResponse = restTemplate.exchange(
                baseUrl + "/tournament/" + tournamentId + "/register/" + userId,
                HttpMethod.PUT,
                new HttpEntity<>(headers),
                String.class
        );
        assertEquals(HttpStatus.OK, registerResponse.getStatusCode());
    }

    private String extractUserId(String response) {
        // Extract just the ID before any timestamp
        return response.substring(
                response.indexOf("ID: ") + 4,
                response.indexOf(" at:")
        ).trim();
    }
    private String extractTournamentId(String response) {
        // Extract just the ID before the "at:" text
        return response.substring(
                response.indexOf("ID: ") + 4,
                response.indexOf(" at:")
        ).trim();
    }
}