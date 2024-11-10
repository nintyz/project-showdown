package com.projectshowdown.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import com.projectshowdown.dto.UserDTO;

public class TournamentTest {

    private Tournament tournament;
    private List<UserDTO> mockPlayers;

    @BeforeEach
    public void setUp() {
        // Create a list to hold all the mock players
        mockPlayers = new ArrayList<>();

        for (int i = 1; i <= 32; i++) {
            UserDTO mockPlayer = Mockito.mock(UserDTO.class);
            Player mockPlayerDetails = Mockito.mock(Player.class);
            Mockito.when(mockPlayer.getPlayerDetails()).thenReturn(mockPlayerDetails);
            Mockito.when(mockPlayer.getId()).thenReturn("Player" + i);
            Mockito.when(mockPlayerDetails.calculateMMR()).thenReturn((double) (1200 + i * 10));
            mockPlayers.add(mockPlayer);
        }

        tournament = new Tournament();
        tournament.setId("T001");
        tournament.setName("Showdown Tournament");
        tournament.setYear(2024);
        tournament.setType("Single Elimination");
        tournament.setVenue("Stadium");
        tournament.setDateTime("2024-10-15");
        tournament.setNumPlayers(32);
        tournament.setStatus("ongoing");
        tournament.setMinMMR(1000);
        tournament.setMaxMMR(1500);
        tournament.setRounds(new ArrayList<>());
    }

    @Test
    public void testConstructorAndGetters() {
        assertNotNull(tournament);
        assertEquals("T001", tournament.getId());
        assertEquals("Showdown Tournament", tournament.getName());
        assertEquals(2024, tournament.getYear());
        assertEquals("Single Elimination", tournament.getType());
        assertEquals("Stadium", tournament.getVenue());
        assertEquals("2024-10-15", tournament.getDateTime());
        assertEquals(32, tournament.getNumPlayers());
        assertEquals("ongoing", tournament.getStatus());
        assertEquals(1000, tournament.getMinMMR());
        assertEquals(1500, tournament.getMaxMMR());
        assertNotNull(tournament.getUsers());
        assertTrue(tournament.getUsers().isEmpty());
    }

    @Test
    public void testAddPlayerWithinMMRRange() {
        // Add players within MMR range
        for (UserDTO player : mockPlayers.subList(0, 30)) {
            assertTrue(tournament.checkUserEligibility(player));
            tournament.getUsers().add(player.getId());
        }
        assertEquals(30, tournament.getUsers().size());
    }

    @Test
    public void testAddPlayerOutsideMMRRange() {
        // Test player with MMR outside range
        UserDTO outsideMMRPlayer = mockPlayers.get(31); // Last player has MMR 1520
        assertFalse(tournament.checkUserEligibility(outsideMMRPlayer));
    }

    @Test
    public void testTotalMatches() {
        // Create and add a round with matches
        List<String> matchIds = new ArrayList<>();
        matchIds.add("match1");
        matchIds.add("match2");

        Round round = new Round();
        round.setName("Round 1");
        round.setMatches(matchIds);

        List<Round> rounds = new ArrayList<>();
        rounds.add(round);
        tournament.setRounds(rounds);

        assertEquals(2, tournament.totalMatches());
    }

    @Test
    public void testSettersAndGetters() {
        tournament.setId("T002");
        tournament.setName("New Tournament");
        tournament.setYear(2025);
        tournament.setType("Double Elimination");
        tournament.setVenue("New Stadium");
        tournament.setDateTime("2025-10-15");
        tournament.setNumPlayers(16);
        tournament.setStatus("completed");
        tournament.setMinMMR(1100);
        tournament.setMaxMMR(1600);

        assertEquals("T002", tournament.getId());
        assertEquals("New Tournament", tournament.getName());
        assertEquals(2025, tournament.getYear());
        assertEquals("Double Elimination", tournament.getType());
        assertEquals("New Stadium", tournament.getVenue());
        assertEquals("2025-10-15", tournament.getDateTime());
        assertEquals(16, tournament.getNumPlayers());
        assertEquals("completed", tournament.getStatus());
        assertEquals(1100, tournament.getMinMMR());
        assertEquals(1600, tournament.getMaxMMR());
    }

    @Test
    public void testConstructorWithRounds() {
        // Create test rounds
        List<Round> testRounds = new ArrayList<>();
        Round round = new Round();
        round.setName("Round 1");
        List<String> matches = new ArrayList<>();
        matches.add("match1");
        matches.add("match2");
        round.setMatches(matches);
        testRounds.add(round);

        // Create tournament
        Tournament tournamentWithRounds = new Tournament("T002", "Test Tournament", 2024,
                "Single Elimination", "Venue", "2024-10-15", 32, "ongoing", 1000, 1500, testRounds);

        // Set rounds after construction since constructor creates new ArrayList
        tournamentWithRounds.setRounds(testRounds);

        assertEquals("T002", tournamentWithRounds.getId());
        assertEquals(1, tournamentWithRounds.getRounds().size());
        assertEquals(2, tournamentWithRounds.totalMatches());
    }

    @Test
    public void testCheckUserEligibilityWithinRange() {
        UserDTO mockUser = Mockito.mock(UserDTO.class);
        Player mockPlayerDetails = Mockito.mock(Player.class);
        Mockito.when(mockUser.getPlayerDetails()).thenReturn(mockPlayerDetails);
        Mockito.when(mockPlayerDetails.calculateMMR()).thenReturn(1250.0);

        assertTrue(tournament.checkUserEligibility(mockUser),
                "User with MMR within range should be eligible");
    }

    @Test
    public void testCheckUserEligibilityBelowRange() {
        UserDTO mockUser = Mockito.mock(UserDTO.class);
        Player mockPlayerDetails = Mockito.mock(Player.class);
        Mockito.when(mockUser.getPlayerDetails()).thenReturn(mockPlayerDetails);
        Mockito.when(mockPlayerDetails.calculateMMR()).thenReturn(900.0);

        assertFalse(tournament.checkUserEligibility(mockUser),
                "User with MMR below range should not be eligible");
    }

    @Test
    public void testCheckUserEligibilityAboveRange() {
        UserDTO mockUser = Mockito.mock(UserDTO.class);
        Player mockPlayerDetails = Mockito.mock(Player.class);
        Mockito.when(mockUser.getPlayerDetails()).thenReturn(mockPlayerDetails);
        Mockito.when(mockPlayerDetails.calculateMMR()).thenReturn(1600.0);

        assertFalse(tournament.checkUserEligibility(mockUser),
                "User with MMR above range should not be eligible");
    }

    @Test
    public void testTotalMatchesWithMultipleRounds() {
        // Create first round
        Round round1 = new Round();
        round1.setName("Round 1");
        List<String> matches1 = new ArrayList<>();
        matches1.add("match1");
        matches1.add("match2");
        round1.setMatches(matches1);

        // Create second round
        Round round2 = new Round();
        round2.setName("Round 2");
        List<String> matches2 = new ArrayList<>();
        matches2.add("match3");
        round2.setMatches(matches2);

        // Add rounds to tournament
        List<Round> rounds = new ArrayList<>();
        rounds.add(round1);
        rounds.add(round2);
        tournament.setRounds(rounds);

        assertEquals(3, tournament.totalMatches(),
                "Total matches should be sum of matches in all rounds");
    }

    @Test
    public void testEmptyTournament() {
        Tournament emptyTournament = new Tournament();
        assertNull(emptyTournament.getId());
        assertNull(emptyTournament.getName());
        assertEquals(0, emptyTournament.getYear());
        assertNull(emptyTournament.getType());
        assertNull(emptyTournament.getVenue());
        assertNull(emptyTournament.getDateTime());
        assertEquals(0, emptyTournament.getNumPlayers());
        assertNull(emptyTournament.getStatus());
        assertEquals(0.0, emptyTournament.getMinMMR());
        assertEquals(0.0, emptyTournament.getMaxMMR());
        assertNotNull(emptyTournament.getUsers());
        assertTrue(emptyTournament.getUsers().isEmpty());
    }

    @Test
    public void testTotalMatchesWithEmptyRounds() {
        tournament.setRounds(new ArrayList<>());
        assertEquals(0, tournament.totalMatches(),
                "Tournament with no rounds should have 0 matches");
    }

    @Test
    public void testAddAndRemoveUsers() {
        List<String> users = new ArrayList<>();
        users.add("user1");
        users.add("user2");

        tournament.setUsers(users);
        assertEquals(2, tournament.getUsers().size());

        users.remove("user1");
        tournament.setUsers(users);
        assertEquals(1, tournament.getUsers().size());
        assertEquals("user2", tournament.getUsers().get(0));
    }


}