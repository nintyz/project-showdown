package com.projectshowdown.service;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.entities.Match;
import com.projectshowdown.entities.Round;
import com.projectshowdown.entities.Tournament;
import com.projectshowdown.exceptions.TournamentNotFoundException;
import com.projectshowdown.events.MatchUpdatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.google.cloud.Timestamp;
import com.google.api.core.ApiFuture;

@ExtendWith(MockitoExtension.class)
public class TournamentServiceTest {

    @InjectMocks
    private TournamentService tournamentService;

    @Mock
    private UserService userService;

    @Mock
    private MatchService matchService;

    @Mock
    private Firestore firestore;

    @Mock
    private DocumentReference docRef;

    @Mock
    private DocumentSnapshot documentSnapshot;

    @Mock
    private WriteResult writeResult;

    private Tournament tournament;
    private UserDTO userDTO;

    @BeforeEach
    public void setup() {
        tournament = new Tournament();
        tournament.setId("testTournamentId");
        tournament.setName("Test Tournament");

        userDTO = new UserDTO();
        userDTO.setId("testUserId");

        when(FirestoreClient.getFirestore()).thenReturn(firestore);
    }

    @Test
    public void testAddTournament() throws ExecutionException, InterruptedException {
        when(firestore.collection("tournaments").document()).thenReturn(docRef);

        // Create a mocked ApiFuture for WriteResult
        ApiFuture<WriteResult> writeResultFuture = mock(ApiFuture.class);
        when(docRef.set(any(Tournament.class))).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(writeResult);

        when(writeResult.getUpdateTime()).thenReturn(Timestamp.now());

        String response = tournamentService.addTournament(tournament, "testUserId");

        assertTrue(response.contains("Tournament created successfully"));
        verify(docRef).set(tournament);
    }

    @Test
    public void testGetAllTournaments() throws ExecutionException, InterruptedException {
        CollectionReference collectionReference = mock(CollectionReference.class);
        Query query = mock(Query.class);
        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        List<QueryDocumentSnapshot> documents = List.of(mock(QueryDocumentSnapshot.class));

        // Mock collection reference to return a Query when .get() is called
        when(firestore.collection("tournaments")).thenReturn(collectionReference);
        when(collectionReference.get()).thenReturn(mock(ApiFuture.class));
        when(collectionReference.get().get()).thenReturn(querySnapshot);

        when(querySnapshot.getDocuments()).thenReturn(documents);

        List<Tournament> tournaments = tournamentService.getAllTournaments();

        assertNotNull(tournaments);
        verify(firestore.collection("tournaments")).get();
    }

    @Test
    public void testGetTournamentSuccess() throws ExecutionException, InterruptedException {
        when(firestore.collection("tournaments").document("testTournamentId")).thenReturn(docRef);

        // Create a mocked ApiFuture for DocumentSnapshot
        ApiFuture<DocumentSnapshot> documentSnapshotFuture = mock(ApiFuture.class);
        when(docRef.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);

        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(Tournament.class)).thenReturn(tournament);

        Tournament result = tournamentService.getTournament("testTournamentId");

        assertNotNull(result);
        assertEquals("testTournamentId", result.getId());
        verify(docRef).get();
    }

    @Test
    public void testGetTournamentNotFound() throws ExecutionException, InterruptedException {
        when(firestore.collection("tournaments").document("nonExistentId")).thenReturn(docRef);

        // Create a mocked ApiFuture for DocumentSnapshot
        ApiFuture<DocumentSnapshot> documentSnapshotFuture = mock(ApiFuture.class);
        when(docRef.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);

        when(documentSnapshot.exists()).thenReturn(false);

        assertThrows(TournamentNotFoundException.class, () -> tournamentService.getTournament("nonExistentId"));
        verify(docRef).get();
    }

    @Test
    public void testRegisterUser_SuccessfulRegistration() throws ExecutionException, InterruptedException {
        List<String> users = new ArrayList<>();
        tournament.setUsers(users);

        when(userService.getUser("testUserId")).thenReturn(userDTO);
        when(firestore.collection("tournaments").document("testTournamentId")).thenReturn(docRef);

        // Create a mocked ApiFuture for DocumentSnapshot
        ApiFuture<DocumentSnapshot> documentSnapshotFuture = mock(ApiFuture.class);
        when(docRef.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);

        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(Tournament.class)).thenReturn(tournament);

        // Create a mocked ApiFuture for WriteResult
        ApiFuture<WriteResult> writeResultFuture = mock(ApiFuture.class);
        when(docRef.update(anyString(), any())).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(writeResult);

        String result = tournamentService.registerUser("testTournamentId", "testUserId");

        assertTrue(result.contains("has successfully joined Tournament"));
        verify(docRef).update("users", List.of("testUserId"));
    }

    @Test
    public void testCancelRegistration_UserNotRegistered() throws ExecutionException, InterruptedException {
        List<String> users = new ArrayList<>();
        tournament.setUsers(users);

        when(firestore.collection("tournaments").document("testTournamentId")).thenReturn(docRef);

        // Create a mocked ApiFuture for DocumentSnapshot
        ApiFuture<DocumentSnapshot> documentSnapshotFuture = mock(ApiFuture.class);
        when(docRef.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);

        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(Tournament.class)).thenReturn(tournament);

        String result = tournamentService.cancelRegistration("testTournamentId", "testUserId");

        assertEquals("You are not registered to this event!", result);
        verify(docRef, never()).update(anyString(), any());
    }

    @Test
    public void testHandleMatchUpdated_FinalRound() throws ExecutionException, InterruptedException {
        MatchUpdatedEvent event = new MatchUpdatedEvent(this, "testTournamentId", new Match());

        List<Round> rounds = new ArrayList<>();
        Round finalRound = new Round("Finals", List.of("match1"));
        rounds.add(finalRound);
        tournament.setRounds(rounds);

        when(tournamentService.getTournament("testTournamentId")).thenReturn(tournament);

        tournamentService.handleMatchUpdated(event);

        verify(userService, times(2)).updateUser(anyString(), any());
    }

    @Test
    public void testUpdateTournament() throws ExecutionException, InterruptedException {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("status", "Cancelled");

        when(firestore.collection("tournaments").document("testTournamentId")).thenReturn(docRef);

        // Create a mocked ApiFuture for DocumentSnapshot
        ApiFuture<DocumentSnapshot> documentSnapshotFuture = mock(ApiFuture.class);
        when(docRef.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);

        when(documentSnapshot.exists()).thenReturn(true);

        // Create a mocked ApiFuture for WriteResult
        ApiFuture<WriteResult> writeResultFuture = mock(ApiFuture.class);
        when(docRef.update(anyMap())).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(writeResult);

        when(writeResult.getUpdateTime()).thenReturn(Timestamp.now());

        String response = tournamentService.updateTournament("testUserId","testTournamentId", updateData);

        assertTrue(response.contains("updated successfully"));
        verify(docRef).update(anyMap());
    }
}
