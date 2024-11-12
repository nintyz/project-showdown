package com.projectshowdown.controllers;

// import com.google.cloud.firestore.Firestore;
import com.projectshowdown.config.TestSecurityConfig;
import com.projectshowdown.config.TestGoogleServiceConfig;
import com.projectshowdown.configs.JwtUtil;
import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.entities.Player;
import com.projectshowdown.entities.Tournament;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "SUPPORT_EMAIL=admin@gmail.com")
@TestPropertySource(properties = "APP_PASSWORD=Password1!")
@TestPropertySource(properties = "GOOGLE_CONFIG_PATH=/Users/arthurchan/Documents/firebasekey/serviceAccountKey.json")

@Import({TestGoogleServiceConfig.class, TestSecurityConfig.class})
@ActiveProfiles("test")
public class TournamentControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @MockBean
    private JwtUtil jwtUtil;

//     @MockBean
//     private Firestore firestore; // Mock Firestore instead of actual instance

//     @Autowired
//     private TournamentController tournamentController;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private HttpHeaders headers;
    private Tournament testTournament;
    private List<String> createdTournamentIds;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Mockito.when(jwtUtil.getJwtSecret()).thenReturn("mocked_jwt_secret_key");
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
        assertNotNull(response.getBody(), "Response body should not be null");
        assertTrue(response.getBody().contains("Tournament created successfully with ID:"));

        // Extract and store tournament ID for cleanup
        String tournamentId = response.getBody();
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
        createdTournamentIds.add(createResponse.getBody());

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
        String tournamentId = createResponse.getBody();

        // Get the tournament
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.getForEntity(
                baseUrl + "/tournament/" + tournamentId,
                Map.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body should not be null");
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
        String tournamentId = createResponse.getBody();
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
        String tournamentId = createResponse.getBody();
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
        assertNotNull(cancelResponse.getBody(), "Cancel response body should not be null");
        assertTrue(cancelResponse.getBody().contains("updated successfully"));
        
        // Fetch the tournament to verify its status is now "cancelled"
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> tournamentResponse = restTemplate.getForEntity(
                baseUrl + "/tournament/" + tournamentId,
                Map.class
        );

        assertEquals(HttpStatus.OK, tournamentResponse.getStatusCode());
        assertNotNull(tournamentResponse.getBody(), "Tournament response body should not be null");
        assertEquals("cancelled", tournamentResponse.getBody().get("status"));
}

    @Test
    void testRegisterUser() {
        // First create a test user
        Player playerDetails = new Player(1, "2000-01-01", 1500.0, 24, 2500.0, "", "", "");
        UserDTO testUser = new UserDTO(
                "testUserId",
                "testName",
                "testProfileUrl",
                "testEmail",
                "Password1@",
                "player",
                "testTwoFactorSecret",
                playerDetails,
                null, // organizerDetails
                "testVerificationCode",
                null, // verificationCodeExpiresAt
                true // enabled
        );

        HttpEntity<UserDTO> userRequest = new HttpEntity<>(testUser, headers);
        ResponseEntity<String> userResponse = restTemplate.postForEntity(
                baseUrl + "/users",
                userRequest,
                String.class
        );
        String userId = userResponse.getBody();

        // Then create a tournament
        HttpEntity<Tournament> createRequest = new HttpEntity<>(testTournament, headers);
        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                baseUrl + "/tournaments",
                createRequest,
                String.class
        );
        String tournamentId = createResponse.getBody();
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
    
}