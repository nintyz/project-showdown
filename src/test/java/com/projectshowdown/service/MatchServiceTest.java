package com.projectshowdown.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.entities.Match;
import com.projectshowdown.entities.Player;
import com.projectshowdown.events.MatchUpdatedEvent;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private MatchService matchService;

    private Match testMatch;

    @BeforeEach
    public void setUp() {
        testMatch = new Match(
                "match1",
                "tournament1",
                "player1",
                "player2",
                2,
                1,
                100.0,
                "2024-03-20T14:00:00",
                "Round of 16",
                false
        );

        when(firestore.collection("matches")).thenReturn(matchesCollection);
        when(matchesCollection.document(anyString())).thenReturn(documentReference);
    }

    @Test
    void testAddMatch() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> mockedFirestore = mockStatic(FirestoreClient.class)) {
            mockedFirestore.when(FirestoreClient::getFirestore).thenReturn(firestore);

            when(documentReference.set(any(Match.class))).thenReturn(writeResultFuture);

            String resultId = matchService.addMatch(testMatch);

            assertEquals("match1", resultId);
            verify(documentReference).set(testMatch);
        }
    }

    @Test
    void testUpdateMatch() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> mockedFirestore = mockStatic(FirestoreClient.class)) {
            mockedFirestore.when(FirestoreClient::getFirestore).thenReturn(firestore);

            Map<String, Object> updateData = new HashMap<>();
            updateData.put("player1Score", 3);
            updateData.put("player2Score", 1);

            when(documentReference.get()).thenReturn(documentSnapshotFuture);
            when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
            when(documentSnapshot.exists()).thenReturn(true);
            when(documentSnapshot.getString("tournamentId")).thenReturn("tournament1");
            when(documentSnapshot.get("dateTime")).thenReturn("2024-03-20T14:00:00");
            when(documentSnapshot.toObject(Match.class)).thenReturn(testMatch);
            when(documentReference.update(anyMap())).thenReturn(writeResultFuture);

            // Mock WriteResult
            WriteResult writeResult = mock(WriteResult.class);
            when(writeResultFuture.get()).thenReturn(writeResult);
            when(writeResult.getUpdateTime()).thenReturn(Timestamp.now());

            // Mock user service for winner update
            UserDTO winner = new UserDTO();
            winner.setId("player1");
            Player playerDetails = new Player();
            playerDetails.setElo(1200.0);
            playerDetails.setPeakElo(1200.0);
            winner.setPlayerDetails(playerDetails);
            when(userService.getUser(anyString())).thenReturn(winner);

            String result = matchService.updateMatch("match1", updateData);

            assertTrue(result.contains("Match with ID: match1 updated successfully"));
            verify(eventPublisher).publishEvent(any(MatchUpdatedEvent.class));
            verify(userService).updateUser(eq("player1"), anyMap());
        }
    }

    @Test
    void testUpdateMatchDateTime() throws ExecutionException, InterruptedException, MessagingException {
        try (MockedStatic<FirestoreClient> mockedFirestore = mockStatic(FirestoreClient.class)) {
            mockedFirestore.when(FirestoreClient::getFirestore).thenReturn(firestore);

            // Mock tournament collection and document
            CollectionReference tournamentCollection = mock(CollectionReference.class);
            DocumentReference tournamentDocRef = mock(DocumentReference.class);
            DocumentSnapshot tournamentDocSnapshot = mock(DocumentSnapshot.class);
            ApiFuture<DocumentSnapshot> tournamentFuture = mock(ApiFuture.class);

            when(firestore.collection("tournaments")).thenReturn(tournamentCollection);
            when(tournamentCollection.document(anyString())).thenReturn(tournamentDocRef);
            when(tournamentDocRef.get()).thenReturn(tournamentFuture);
            when(tournamentFuture.get()).thenReturn(tournamentDocSnapshot);
            when(tournamentDocSnapshot.exists()).thenReturn(true);
            when(tournamentDocSnapshot.getString("name")).thenReturn("Test Tournament");

            Map<String, Object> updateData = new HashMap<>();
            updateData.put("dateTime", "2024-03-21T15:00:00");

            when(documentReference.get()).thenReturn(documentSnapshotFuture);
            when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
            when(documentSnapshot.exists()).thenReturn(true);
            when(documentSnapshot.getString("tournamentId")).thenReturn("tournament1");
            when(documentSnapshot.getString("player1Id")).thenReturn("player1");
            when(documentSnapshot.getString("player2Id")).thenReturn("player2");
            when(documentSnapshot.get("dateTime")).thenReturn("2024-03-20T14:00:00");
            when(documentSnapshot.toObject(Match.class)).thenReturn(testMatch);
            when(documentReference.update(anyMap())).thenReturn(writeResultFuture);

            // Add WriteResult mock (copied from testUpdateMatch)
            WriteResult writeResult = mock(WriteResult.class);
            when(writeResultFuture.get()).thenReturn(writeResult);
            when(writeResult.getUpdateTime()).thenReturn(Timestamp.now());

            // Mock users for notification
            UserDTO user1 = new UserDTO();
            user1.setEmail("player1@test.com");
            user1.setName("Player 1");
            UserDTO user2 = new UserDTO();
            user2.setEmail("player2@test.com");
            user2.setName("Player 2");
            when(userService.getUser("player1")).thenReturn(user1);
            when(userService.getUser("player2")).thenReturn(user2);

            String result = matchService.updateMatch("match1", updateData);

            assertTrue(result.contains("Match with ID: match1 updated successfully"));
            verify(notificationService, times(2)).notifyMatchDetailsUpdated(
                    anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
            );
        }
    }

    @Test
    void testCheckCurrentRoundCompletion() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> mockedFirestore = mockStatic(FirestoreClient.class)) {
            mockedFirestore.when(FirestoreClient::getFirestore).thenReturn(firestore);

            List<String> matchIds = Arrays.asList("match1", "match2");
            Match match1 = new Match();
            match1.setCompleted(true);
            Match match2 = new Match();
            match2.setCompleted(true);

            when(documentReference.get()).thenReturn(documentSnapshotFuture);
            when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
            when(documentSnapshot.exists()).thenReturn(true);
            when(documentSnapshot.toObject(Match.class)).thenReturn(match1, match2);

            boolean result = matchService.checkCurrentRoundCompletion(matchIds);

            assertTrue(result);
        }
    }

    @Test
    void testGetMatch() throws ExecutionException, InterruptedException {
        try (MockedStatic<FirestoreClient> mockedFirestore = mockStatic(FirestoreClient.class)) {
            mockedFirestore.when(FirestoreClient::getFirestore).thenReturn(firestore);

            when(documentReference.get()).thenReturn(documentSnapshotFuture);
            when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
            when(documentSnapshot.exists()).thenReturn(true);
            when(documentSnapshot.toObject(Match.class)).thenReturn(testMatch);

            Match result = matchService.getMatch("match1");

            assertNotNull(result);
            assertEquals("match1", result.getId());
            assertEquals("tournament1", result.getTournamentId());
        }
    }

    @Test
    void testGetMatchNotFound() {
        try (MockedStatic<FirestoreClient> mockedFirestore = mockStatic(FirestoreClient.class)) {
            mockedFirestore.when(FirestoreClient::getFirestore).thenReturn(firestore);

            when(documentReference.get()).thenReturn(documentSnapshotFuture);
            when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
            when(documentSnapshot.exists()).thenReturn(false);

            assertThrows(RuntimeException.class, () -> matchService.getMatch("nonexistent"));
        } catch (ExecutionException | InterruptedException e) {
            fail("Should not throw these exceptions");
        }
    }
}
