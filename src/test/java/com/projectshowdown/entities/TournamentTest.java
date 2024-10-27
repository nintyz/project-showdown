package com.projectshowdown.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

public class TournamentTest {

    private Tournament tournament;
    private List<User> mockPlayers;

    @BeforeEach
    public void setUp() {
        // Create a list to hold all the mock players
        mockPlayers = new ArrayList<>();

        for (int i = 1; i <= 32; i++) {
            User mockPlayer = Mockito.mock(User.class);
            Player mockPlayerDetails = Mockito.mock(Player.class);
            Mockito.when(mockPlayer.getPlayerDetails()).thenReturn(mockPlayerDetails);
            Mockito.when(mockPlayer.getId()).thenReturn("Player" + i); // Mock player ID
            Mockito.when(mockPlayerDetails.calculateMMR()).thenReturn((double) (1200 + i * 10)); // MMR between 1210 and 1520
            mockPlayers.add(mockPlayer);
        }

        // Set MMR range between 1000 and 1500
        tournament = new Tournament("T001", "Showdown Tournament", 2024, "Single Elimination", "Stadium", "2024-10-15", 32, "ongoing", 1000, 1500);
    }

    @Test
    public void testAddPlayerWithinMMRRange() {
        // Act & Assert: Players with MMR between 1000 and 1500 should be added
        for (User player : mockPlayers.subList(0, 30)) { // First 30 players are within the range
            tournament.addUser(player);
        }
        assertEquals(30, tournament.getUsers().size(), "Tournament should have added 30 players within the MMR range.");
    }

    @Test
    public void testAddPlayerOutsideMMRRange() {
        // Act & Assert: Players outside the MMR range should throw an exception
        User outsideMMRPlayer = mockPlayers.get(31); // Last player has MMR 1520, which is out of range
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> tournament.addUser(outsideMMRPlayer));
        assertEquals("Player with MMR 1520.0 is not eligible for this tournament (Allowed range: 1000.0 - 1500.0)", exception.getMessage());
    }

    @Test
    public void testGetSeedings() {
        // Add only players within the MMR range
        for (User player : mockPlayers.subList(0, 30)) {
            tournament.addUser(player);
        }

        // Act
        TreeMap<Integer, User> seedings = tournament.getSeedings();

        // Assert
        assertNotNull(seedings, "Seedings should not be null.");
        assertEquals(30, seedings.size(), "Seedings should contain 30 players.");

        // Verify the correct seeding based on calculated MMR
        for (int i = 0; i < 30; i++) {
            User expectedPlayer = mockPlayers.get(i);
            assertEquals(expectedPlayer, seedings.get(i + 1), "Player with MMR should be seeded correctly.");
        }
    }

    @Test
    public void testCreateMatches() {
        // Add players within MMR range
        for (User player : mockPlayers.subList(0, 32)) {
            tournament.addUser(player);
        }

        // Act
        List<Match> matches = tournament.createMatches();

        // Assert
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

        // Validate the match IDs according to the specified mapping
        int[] expectedMatchNumbers = {1, 16, 3, 14, 5, 12, 7, 10, 2, 15, 4, 13, 6, 11, 8, 9};

        for (int i = 0; i < matches.size(); i++) {
            Match match = matches.get(i);
            String expectedMatchId = "Showdown Tournament_Round 1_" + expectedMatchNumbers[i];
            assertEquals(expectedMatchId, match.getMatchId(), "Match ID should be generated correctly for match number " + expectedMatchNumbers[i]);
        }
    }
}
