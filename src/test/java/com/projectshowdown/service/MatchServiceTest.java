package com.projectshowdown.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.projectshowdown.entities.Match;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private Firestore firestore;

    @Mock
    private CollectionReference matchesCollection;

    @Mock
    private DocumentReference documentReference;

    @Mock
    private ApiFuture<WriteResult> writeResultFuture;

    @Mock
    private ApiFuture<DocumentSnapshot> documentSnapshotFuture;

    @Mock
    private DocumentSnapshot documentSnapshot;

    @InjectMocks
    private MatchService matchService;

    @BeforeEach
    public void setUp() {
        // Mock FirestoreClient to return the mocked Firestore instance
        Mockito.mockStatic(FirestoreClient.class);
        when(FirestoreClient.getFirestore()).thenReturn(firestore);
        
        // Additional setup
        when(firestore.collection("matches")).thenReturn(matchesCollection);
        when(matchesCollection.document(anyString())).thenReturn(documentReference);
    }

    @Test
    void testAddMatch() throws ExecutionException, InterruptedException {
        Match testMatch = new Match();
        testMatch.setId("testMatchId");
        when(documentReference.set(testMatch)).thenReturn(writeResultFuture);

        String resultId = matchService.addMatch(testMatch);

        assertEquals("testMatchId", resultId);
        verify(documentReference).set(testMatch);
    }

    @Test
    void testCheckCurrentRoundCompletion() throws ExecutionException, InterruptedException {
        List<String> matchIds = Arrays.asList("match1", "match2");
        Match completedMatch = new Match("match1", "Test_Tournament_Id", "Test_player1_Id", "Test_player2_Id", 2, 1, 1000.0, "2024-12-31", "Round Of 16", true);
        Match incompleteMatch = new Match("match2", "Test_Tournament_Id", "Test_player1_Id", "Test_player2_Id", 2, 1, 1000.0, "2024-12-31", "Round Of 16", false);

        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(Match.class)).thenReturn(completedMatch, incompleteMatch);

        boolean result = matchService.checkCurrentRoundCompletion(matchIds);
        assertFalse(result);
    }

    @Test
    void testGetMatches() throws ExecutionException, InterruptedException {
        List<String> matchIds = Arrays.asList("match1", "match2");
        Match match1 = new Match("match1", "Test_Tournament_Id", "Test_player1_Id", "Test_player2_Id", 2, 1, 1000.0, "2024-12-31", "Round Of 16", true);
        Match match2 = new Match("match2", "Test_Tournament_Id", "Test_player1_Id", "Test_player2_Id", 2, 1, 1000.0, "2024-12-31", "Round Of 16", true);

        when(documentReference.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(Match.class)).thenReturn(match1, match2);

        List<Match> results = matchService.getMatches(matchIds);

        assertEquals(2, results.size());
        assertEquals("match1", results.get(0).getId());
        assertEquals("match2", results.get(1).getId());
    }
}
