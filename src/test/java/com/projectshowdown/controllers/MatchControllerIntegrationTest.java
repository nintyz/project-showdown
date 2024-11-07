// package com.projectshowdown.controllers;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.projectshowdown.config.TestSecurityConfig;
// import com.projectshowdown.dto.UserDTO;
// import com.projectshowdown.entities.Player;
// import com.projectshowdown.entities.Tournament;
// import com.projectshowdown.entities.Match;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.web.client.TestRestTemplate;
// import org.springframework.boot.test.web.server.LocalServerPort;
// import org.springframework.context.annotation.Import;
// import org.springframework.http.*;
// import org.springframework.test.context.ActiveProfiles;

// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import static org.junit.jupiter.api.Assertions.*;

// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @Import(TestSecurityConfig.class)
// @ActiveProfiles("test")
// public class MatchControllerIntegrationTest {

//     @LocalServerPort
//     private int port;

//     @Autowired
//     private TestRestTemplate restTemplate;

//     @Autowired
//     private ObjectMapper objectMapper;

//     private String baseUrl;
//     private HttpHeaders headers;
//     private Match testMatch;
//     private List<String> createdMatchIds;

//     @BeforeEach
//     void setUp() {
//         baseUrl = "http://localhost:" + port;
//         headers = new HttpHeaders();
//         headers.setContentType(MediaType.APPLICATION_JSON);
//         createdMatchIds = new ArrayList<>();

//         testMatch = new Match(
//             "Test_Match_Id",
//             "Test_Tournament_Id",
//             "Test_player1_Id",
//             "Test_player2_Id",
//             2, // player 1 score 
//             1, // player 2 score
//             1000.0, // MMR Difference
//             "2024-12-31", // match date
//             "Round Of 16",
//             true // whether the match is completed
//         );
//     }

//     @Test
//     void testAddMatch() throws Exception {
//         HttpEntity<Match> request = new HttpEntity<>(testMatch, headers);
//         ResponseEntity<String> response = restTemplate.postForEntity(
//                 baseUrl + "/matches",
//                 request,
//                 String.class
//         );
//         System.out.println(response.getBody());
//         assertEquals(HttpStatus.CREATED, response.getStatusCode());
//         assertTrue(response.getBody().contains("Match created successfully with ID:"));

//         // Extract and store match ID for cleanup
//         String matchId = extractMatchId(response.getBody());
//         createdMatchIds.add(matchId);
//     }

//     @Test
//     void testGetMatches() {
//         // First create a match
//         HttpEntity<Match> createRequest = new HttpEntity<>(testMatch, headers);
//         ResponseEntity<String> createResponse = restTemplate.postForEntity(
//                 baseUrl + "/match",
//                 createRequest,
//                 String.class
//         );
//         System.out.println(createResponse.getBody());
//         createdMatchIds.add(extractTournamentId(createResponse.getBody()));

//         // Get all Matches
//         ResponseEntity<String> response = restTemplate.getForEntity(
//                 baseUrl + "/matches",
//                 String.class
//         );

//         assertEquals(HttpStatus.OK, response.getStatusCode());
//         assertNotNull(response.getBody());
//     }

//     @Test
//     void testUpdateMatch() throws Exception {
//         // First create a Match
//         HttpEntity<Match> createRequest = new HttpEntity<>(testMatch, headers);
//         ResponseEntity<String> createResponse = restTemplate.postForEntity(
//                 baseUrl + "/matches",
//                 createRequest,
//                 String.class
//         );
//         String matchId = extractMatchId(createResponse.getBody());
//         createdMatchIds.add(matchId);

//         // Update match data
//         Map<String, Object> updateData = new HashMap<>();
//         updateData.put("player1Score", 1);
//         updateData.put("player2Score", 2);
//         updateData.put("date", "2024-12-31");
//         updateData.put("date", "2024-12-31");
//         updateData.put("stage", "Round Of 16");
//         updateData.put("completed", true);

//         HttpEntity<Map<String, Object>> updateRequest = new HttpEntity<>(updateData, headers);
//         ResponseEntity<String> updateResponse = restTemplate.exchange(
//                 baseUrl + "/match/" + matchId,
//                 HttpMethod.PUT,
//                 updateRequest,
//                 String.class
//         );

//         assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
//     }
// }
