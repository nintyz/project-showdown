package com.projectshowdown.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.projectshowdown.entities.Match;
import com.projectshowdown.events.MatchUpdatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class MatchServiceTest {

    @InjectMocks
    private MatchService matchService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference matchesCollection;

    @Mock
    private DocumentReference documentReference;

    @Mock
    private DocumentSnapshot documentSnapshot;

    @Mock
    private ApiFuture<WriteResult> writeResultFuture;

    @Mock
    private WriteResult writeResult;

    @Mock
    private ApiFuture<DocumentSnapshot> documentSnapshotFuture;

    private Match testMatch;

    @BeforeEach
    void setUp() {
        // Set up a sample Match object
        testMatch = new Match("Test_Match_Id", "Test_Tournament_Id", "Test_player1_Id", "Test_player2_Id",
                2, 1, 1000.0, "2024-12-31", "Round Of 16", true);
        
        // Mock Firestore setup
        when(firestore.collection("matches")).thenReturn(matchesCollection);
        when(matchesCollection.document(testMatch.getId())).thenReturn(documentReference);
    }

    @Test
    void testAddMatch() throws ExecutionException, InterruptedException {
        when(documentReference.set(testMatch)).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(writeResult);

        String matchId = matchService.addMatch(testMatch);

        assertEquals(testMatch.getId(), matchId);
        verify(documentReference).set(testMatch);
    }

    @Test
    void testUpdateMatch() throws ExecutionException, InterruptedException {
        Map<String, Object> matchData = new HashMap<>();
        matchData.put("player1Score", 3);
        matchData.put("player2Score", 2);

        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.get("tournamentId")).thenReturn("Test_Tournament_Id");

        when(documentReference.update(matchData)).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(writeResult);

        Match match = new Match("Test_Match_Id", "Test_Tournament_Id", "Test_player1_Id", "Test_player2_Id",
                2, 1, 1000.0, "2024-12-31", "Round Of 16", true);
        
        when(documentSnapshot.toObject(Match.class)).thenReturn(match);

        String result = matchService.updateMatch(testMatch.getId(), matchData);

        assertTrue(result.contains("updated successfully"));
        verify(documentReference).update(matchData);
        verify(eventPublisher).publishEvent(any(MatchUpdatedEvent.class));
    }

    @Test
    void testCheckCurrentRoundCompletion() throws ExecutionException, InterruptedException {
        List<String> currentRound = Arrays.asList("match1", "match2");

        Match completedMatch = new Match("match1", "Test_Tournament_Id", "Test_player1_Id", "Test_player2_Id",
                2, 1, 1000.0, "2024-12-31", "Round Of 16", true);

        when(matchesCollection.document("match1")).thenReturn(documentReference);
        when(matchesCollection.document("match2")).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(Match.class)).thenReturn(completedMatch);

        boolean allCompleted = matchService.checkCurrentRoundCompletion(currentRound);

        assertTrue(allCompleted);
        verify(documentReference, times(2)).get();
    }

    @Test
    void testGetMatch() throws ExecutionException, InterruptedException {
        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(Match.class)).thenReturn(testMatch);

        Match result = matchService.getMatch(testMatch.getId());

        assertNotNull(result);
        assertEquals(testMatch.getId(), result.getId());
        verify(documentReference).get();
    }

    @Test
    void testGetMatches() throws ExecutionException, InterruptedException {
        List<String> matchIds = Arrays.asList("match1", "match2");

        Match match1 = new Match("match1", "Test_Tournament_Id", "Test_player1_Id", "Test_player2_Id",
                2, 1, 1000.0, "2024-12-31", "Round Of 16", true);
        Match match2 = new Match("match2", "Test_Tournament_Id", "Test_player3_Id", "Test_player4_Id",
                1, 2, 800.0, "2024-12-31", "Round Of 16", true);

        when(matchesCollection.document("match1")).thenReturn(documentReference);
        when(matchesCollection.document("match2")).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(Match.class)).thenReturn(match1, match2);

        List<Match> matches = matchService.getMatches(matchIds);

        assertEquals(2, matches.size());
        verify(documentReference, times(2)).get();
    }
}