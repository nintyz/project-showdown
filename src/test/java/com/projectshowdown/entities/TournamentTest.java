package com.projectshowdown.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

public class TournamentTest {

    private Tournament tournament;
    private List<Player> mockPlayers;

    @BeforeEach
    public void setUp() {
        // Initialize the tournament object
        tournament = new Tournament("T001", "Showdown Tournament", 2024, "Single Elimination", "Stadium", new Date());

        // Create a list to hold all the mock players
        mockPlayers = new ArrayList<>();

        // Mock 32 player objects and add them to the tournament
        for (int i = 1; i <= 32; i++) {
            Player mockPlayer = Mockito.mock(Player.class);
            // Assign unique MMR values to the players for testing purposes
            Mockito.when(mockPlayer.calculateMMR()).thenReturn((double) (3200 - i * 100)); // Example MMRs: 3200, 3100, ..., 1000
            mockPlayers.add(mockPlayer);
            tournament.addPlayer(mockPlayer); // Add each mock player to the tournament
        }
    }

    @Test
    public void testAddPlayer() {
        assertEquals(32, tournament.getPlayers().size(), "Tournament should have exactly 32 players.");
    }

    @Test
    public void testGetSeedings() {
        TreeMap<Integer, Player> seedings = tournament.getSeedings();
        assertNotNull(seedings, "Seedings should not be null.");
        assertEquals(32, seedings.size(), "Seedings should contain 32 players.");
        
        // Verify the correct seeding based on calculated MMR
        for (int i = 0; i < mockPlayers.size(); i++) {
            Player expectedPlayer = mockPlayers.get(i);
            assertEquals(expectedPlayer, seedings.get(i + 1), "Player with MMR should be seeded correctly.");
        }
    }

    @Test
    public void testCreateMatches() {
        List<Match> matches = tournament.createMatches();
        assertNotNull(matches, "Matches should not be null.");
        assertEquals(16, matches.size(), "There should be 16 matches in the first round.");

        // Check the first match
        Match firstMatch = matches.get(0);
        assertEquals(mockPlayers.get(0).getId(), firstMatch.getPlayer1Id(), "Player 1 of the first match should be the highest seeded player.");
        assertEquals(mockPlayers.get(31).getId(), firstMatch.getPlayer2Id(), "Player 2 of the first match should be the lowest seeded player.");

        // Check the second match
        Match secondMatch = matches.get(1);
        assertEquals(mockPlayers.get(1).getId(), secondMatch.getPlayer1Id(), "Player 1 of the second match should be the second highest seeded player.");
        assertEquals(mockPlayers.get(30).getId(), secondMatch.getPlayer2Id(), "Player 2 of the second match should be the second lowest seeded player.");
    }
}