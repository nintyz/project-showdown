package com.projectshowdown.service;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.entities.Match;
import com.projectshowdown.entities.Round;
import com.projectshowdown.entities.Tournament;
import com.projectshowdown.entities.User;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

        // Creates a mocked document reference whenever the .collection method is called
        when(firestore.collection("tournaments").document()).thenReturn(docRef);

        // Mocks the behaviour of set which saves data to the document
        // CompletableFuture simulates a response from Firestore, which contains info about write operation
        when(docRef.set(any(Tournament.class))).thenReturn(CompletableFuture.completedFuture(writeResult));

        // Should return the current timestamp of the action
        when(writeResult.getUpdateTime()).thenReturn(Timestamp.now());

        // These are needed to simulate the firestore

        String response = tournamentService.addTournament(tournament);

        assertTrue(response.contains("Tournament created successfully"));
        verify(docRef).set(tournament);
    }

    @Test
    public void testGetAllTournaments() throws ExecutionException, InterruptedException {
        Query query = mock(Query.class);
        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        List<QueryDocumentSnapshot> documents = List.of(mock(QueryDocumentSnapshot.class));

        when(firestore.collection("tournaments")).thenReturn(query);
        when(query.get()).thenReturn(CompletableFuture.completedFuture(querySnapshot));
        when(querySnapshot.getDocuments()).thenReturn(documents);

        List<Tournament> tournaments = tournamentService.getAllTournaments();

        assertNotNull(tournaments);
        verify(firestore.collection("tournaments")).get();
    }

    @Test
    public void testGetTournamentSuccess() throws ExecutionException, InterruptedException {
        when(firestore.collection("tournaments").document("testTournamentId")).thenReturn(docRef);
        when(docRef.get()).thenReturn(CompletableFuture.completedFuture(documentSnapshot));
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
        when(docRef.get()).thenReturn(CompletableFuture.completedFuture(documentSnapshot));
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
        when(docRef.get()).thenReturn(CompletableFuture.completedFuture(documentSnapshot));
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(Tournament.class)).thenReturn(tournament);
        when(docRef.update(anyString(), any())).thenReturn(CompletableFuture.completedFuture(writeResult));

        String result = tournamentService.registerUser("testTournamentId", "testUserId");

        assertTrue(result.contains("has successfully joined Tournament"));
        verify(docRef).update("users", List.of("testUserId"));
    }

    @Test
    public void testCancelRegistration_UserNotRegistered() throws ExecutionException, InterruptedException {
        List<String> users = new ArrayList<>();
        tournament.setUsers(users);

        when(firestore.collection("tournaments").document("testTournamentId")).thenReturn(docRef);
        when(docRef.get()).thenReturn(CompletableFuture.completedFuture(documentSnapshot));
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(Tournament.class)).thenReturn(tournament);

        String result = tournamentService.cancelRegistration("testTournamentId", "testUserId");

        assertEquals("You are not registered to this event!", result);
        verify(docRef, never()).update(anyString(), any());
    }

    @Test
    public void testHandleMatchUpdated_FinalRound() throws ExecutionException, InterruptedException {
        MatchUpdatedEvent event = new MatchUpdatedEvent("testTournamentId", new Match());

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
        when(docRef.get()).thenReturn(CompletableFuture.completedFuture(documentSnapshot));
        when(documentSnapshot.exists()).thenReturn(true);
        when(docRef.update(anyMap())).thenReturn(CompletableFuture.completedFuture(writeResult));
        when(writeResult.getUpdateTime()).thenReturn(Timestamp.now());

        String response = tournamentService.updateTournament("testTournamentId", updateData);

        assertTrue(response.contains("updated successfully"));
        verify(docRef).update(anyMap());
    }

}
