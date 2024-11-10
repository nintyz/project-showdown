package com.projectshowdown.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectshowdown.config.TestSecurityConfig;
import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.entities.Player;
import com.projectshowdown.entities.Tournament;
import com.projectshowdown.events.MatchUpdatedEvent;
import com.projectshowdown.entities.Match;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
public class MatchControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;
    private HttpHeaders headers;
    private Match testMatch;
    private List<String> createdMatchIds;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        createdMatchIds = new ArrayList<>();

        testMatch = new Match(
            "Test_Match_Id",
            "Test_Tournament_Id",
            "Test_player1_Id",
            "Test_player2_Id",
            2, // player 1 score 
            1, // player 2 score
            1000.0, // MMR Difference
            "2024-12-31", // match date
            "Round Of 16",
            true // whether the match is completed
        );
    }

    @AfterEach
    void tearDown() {
        createdMatchIds.forEach(id -> restTemplate.delete(baseUrl + "/matches/" + id));
        createdMatchIds.clear();
    }

    @Test
    void testUpdateTournament() throws Exception {
        // First create a Match
        HttpEntity<Match> createRequest = new HttpEntity<>(testMatch, headers);
        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                baseUrl + "/matches",
                createRequest,
                String.class
        );
        String matchId = extractMatchId(createResponse.getBody());
        createdMatchIds.add(matchId);

        // Update match data
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("player1Score", 1);
        updateData.put("player2Score", 2);
        updateData.put("date", "2024-12-31");
        updateData.put("date", "2024-12-31");
        updateData.put("stage", "Quarter Finals");
        updateData.put("completed", true);

        HttpEntity<Map<String, Object>> updateRequest = new HttpEntity<>(updateData, headers);
        ResponseEntity<String> updateResponse = restTemplate.exchange(
                baseUrl + "/match/" + matchId,
                HttpMethod.PUT,
                updateRequest,
                String.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertTrue(updateResponse.getBody().contains("updated successfully"));

        // Fetch and verify updated match details
        ResponseEntity<Match> getResponse = restTemplate.getForEntity(
                baseUrl + "/matches/" + matchId,
                Match.class
        );
        Match updatedMatch = getResponse.getBody();

        assertEquals(1, updatedMatch.getPlayer1Score());
        assertEquals(2, updatedMatch.getPlayer2Score());
        assertEquals("Quarter Finals", updatedMatch.getStage());
        assertTrue(updatedMatch.isCompleted());
    }

    @Test
    void testUpdateTournamentEventPublished() throws Exception {
        // Create a Match
        String matchId = createMatch();

        // Update match data
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("player1Score", 1);
        updateData.put("player2Score", 2);
        updateData.put("completed", true);

        HttpEntity<Map<String, Object>> updateRequest = new HttpEntity<>(updateData, headers);
        restTemplate.exchange(
                baseUrl + "/match/" + matchId,
                HttpMethod.PUT,
                updateRequest,
                String.class
        );

        // Verify that the event was published
        verify(eventPublisher, times(1)).publishEvent(any(MatchUpdatedEvent.class));
    }

    private String createMatch() {
        HttpEntity<Match> createRequest = new HttpEntity<>(testMatch, headers);
        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                baseUrl + "/matches",
                createRequest,
                String.class
        );
        String matchId = extractMatchId(createResponse.getBody());
        createdMatchIds.add(matchId);
        return matchId;
    }

    private String extractMatchId(String responseBody) {
        // try {
        //     Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
        //     return (String) responseMap.get("Match ID"); 
        // } catch (Exception e) {
        //     throw new RuntimeException("Failed to parse match ID from response body", e);
        // }
        return "extractedMatchId";
    }
}
