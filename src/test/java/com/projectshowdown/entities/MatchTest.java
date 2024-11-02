package com.projectshowdown.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MatchTest {

    @Test
    void noArgsConstructor_shouldCreateEmptyMatch() {
        Match match = new Match();
        assertNull(match.getId());
        assertNull(match.getTournamentId());
        assertNull(match.getPlayer1Id());
        assertNull(match.getPlayer2Id());
        assertEquals(0, match.getPlayer1Score());
        assertEquals(0, match.getPlayer2Score());
        assertEquals(0.0, match.getMmrDifference());
        assertNull(match.getDate());
        assertNull(match.getStage());
        assertFalse(match.isCompleted());
    }

    @Test
    void allArgsConstructor_shouldCreateMatchWithAllFields() {
        Match match = new Match("1", "t1", "p1", "p2", 3, 1, 100.0, "2024-03-20", "finals", true);

        assertEquals("1", match.getId());
        assertEquals("t1", match.getTournamentId());
        assertEquals("p1", match.getPlayer1Id());
        assertEquals("p2", match.getPlayer2Id());
        assertEquals(3, match.getPlayer1Score());
        assertEquals(1, match.getPlayer2Score());
        assertEquals(100.0, match.getMmrDifference());
        assertEquals("2024-03-20", match.getDate());
        assertEquals("finals", match.getStage());
        assertTrue(match.isCompleted());
    }

    @Test
    void setters_shouldUpdateAllFields() {
        Match match = new Match();

        match.setId("1");
        match.setTournamentId("t1");
        match.setPlayer1Id("p1");
        match.setPlayer2Id("p2");
        match.setPlayer1Score(3);
        match.setPlayer2Score(1);
        match.setMmrDifference(100.0);
        match.setDate("2024-03-20");
        match.setStage("finals");
        match.setCompleted(true);

        assertEquals("1", match.getId());
        assertEquals("t1", match.getTournamentId());
        assertEquals("p1", match.getPlayer1Id());
        assertEquals("p2", match.getPlayer2Id());
        assertEquals(3, match.getPlayer1Score());
        assertEquals(1, match.getPlayer2Score());
        assertEquals(100.0, match.getMmrDifference());
        assertEquals("2024-03-20", match.getDate());
        assertEquals("finals", match.getStage());
        assertTrue(match.isCompleted());
    }

    @Test
    void winnerId_shouldReturnPlayer1Id_whenPlayer1ScoreIsHigher() {
        Match match = new Match("1", "t1", "p1", "p2", 3, 1, 100.0, "2024-03-20", "finals", true);
        assertEquals("p1", match.winnerId());
    }

    @Test
    void winnerId_shouldReturnPlayer2Id_whenPlayer2ScoreIsHigher() {
        Match match = new Match("1", "t1", "p1", "p2", 1, 3, 100.0, "2024-03-20", "finals", true);
        assertEquals("p2", match.winnerId());
    }

    @Test
    void winnerId_shouldReturnPlayer1Id_whenScoresAreEqual() {
        Match match = new Match("1", "t1", "p1", "p2", 2, 2, 100.0, "2024-03-20", "finals", true);
        assertEquals("p1", match.winnerId());
    }

    @Test
    void loserId_shouldReturnPlayer2Id_whenPlayer1ScoreIsHigher() {
        Match match = new Match("1", "t1", "p1", "p2", 3, 1, 100.0, "2024-03-20", "finals", true);
        assertEquals("p2", match.loserId());
    }

    @Test
    void loserId_shouldReturnPlayer1Id_whenPlayer2ScoreIsHigher() {
        Match match = new Match("1", "t1", "p1", "p2", 1, 3, 100.0, "2024-03-20", "finals", true);
        assertEquals("p1", match.loserId());
    }

    @Test
    void loserId_shouldReturnPlayer2Id_whenScoresAreEqual() {
        Match match = new Match("1", "t1", "p1", "p2", 2, 2, 100.0, "2024-03-20", "finals", true);
        assertEquals("p2", match.loserId());
    }
}