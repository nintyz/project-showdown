package com.projectshowdown.service;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.entities.Tournament;
import com.projectshowdown.entities.Round;
import com.projectshowdown.exceptions.TournamentNotFoundException;
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

import com.google.api.core.ApiFuture;

@ExtendWith(MockitoExtension.class)
public class TournamentServiceTest {

    @InjectMocks
    private TournamentService tournamentService;

    @Mock
    private UserService userService;

    @Mock
    private CollectionReference tournamentsCollection;

    @Mock
    private Firestore firestore;

    @Mock
    private DocumentReference docRef;

    @Mock
    private DocumentSnapshot documentSnapshot;

    @Mock
    private ApiFuture<DocumentSnapshot> documentSnapshotFuture;  // Mock the future for the document retrieval

    @Mock
    private ApiFuture<WriteResult> writeResultFuture;  // Mock ApiFuture<WriteResult>

    @Mock
    private WriteResult writeResult;

    @Mock
    Query query;

    @Mock
    QuerySnapshot querySnapshot;

    @Mock
    ApiFuture<QuerySnapshot> querySnapshotFuture;

    @Mock
    QueryDocumentSnapshot queryDocument1;  // Mock QueryDocumentSnapshot

    @Mock
    QueryDocumentSnapshot queryDocument2;  // Mock QueryDocumentSnapshot

    private Tournament tournament;

    @BeforeEach
    public void setUp() {

        // Mock FirestoreClient to return mocked Firestore instance
        mockStatic(FirestoreClient.class);
        when(FirestoreClient.getFirestore()).thenReturn(firestore);

        // Set up Firestore collection and query mock behavior
        when(firestore.collection("tournaments")).thenReturn(tournamentsCollection);
    }

    @Test
    void testGetAllTournaments() throws ExecutionException, InterruptedException {
        // Mock data for tournament documents
        Tournament tournament1 = new Tournament();
        tournament1.setId("Tournament1_Id");
        tournament1.setName("Tournament 1");

        Tournament tournament2 = new Tournament();
        tournament2.setId("Tournament2_Id");
        tournament2.setName("Tournament 2");

        // Set up mock behavior for Firestore API calls
        when(firestore.collection("tournaments")).thenReturn(tournamentsCollection);
        when(tournamentsCollection.get()).thenReturn(querySnapshotFuture); // Return ApiFuture<QuerySnapshot>
        when(querySnapshotFuture.get()).thenReturn(querySnapshot); // Ensure that ApiFuture returns QuerySnapshot

        // Mock query snapshot and document 1 (with QueryDocumentSnapshot)
        when(querySnapshot.getDocuments()).thenReturn(Arrays.asList(queryDocument1, queryDocument2));
        when(queryDocument1.exists()).thenReturn(true);
        when(queryDocument1.toObject(Tournament.class)).thenReturn(tournament1);
        when(queryDocument1.getId()).thenReturn("Tournament1_Id");

        // Mock query snapshot and document 2 (with QueryDocumentSnapshot)
        when(queryDocument2.exists()).thenReturn(true);
        when(queryDocument2.toObject(Tournament.class)).thenReturn(tournament2);
        when(queryDocument2.getId()).thenReturn("Tournament2_Id");

        // Execute the method
        List<Tournament> result = tournamentService.getAllTournaments();

        // Assertions
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Tournament1_Id", result.get(0).getId());
        assertEquals("Tournament 1", result.get(0).getName());
        assertEquals("Tournament2_Id", result.get(1).getId());
        assertEquals("Tournament 2", result.get(1).getName());

        // Verify interactions with Firestore
        verify(tournamentsCollection).get();  // Verifying that the get method was called on the collection
        verify(querySnapshot).getDocuments(); // Verifying that getDocuments was called on the snapshot
    }

    @Test
    void testGetTournamentsByOrganizerId() throws ExecutionException, InterruptedException {

        String ORGANIZER_ID = "organizerId";  // Declare this variable

        // Mock data for tournaments
        Tournament tournament1 = new Tournament();
        tournament1.setId("Tournament1_Id");
        tournament1.setName("Tournament 1");
        tournament1.setOrganizerId(ORGANIZER_ID);

        Tournament tournament2 = new Tournament();
        tournament2.setId("Tournament2_Id");
        tournament2.setName("Tournament 2");
        tournament2.setOrganizerId(ORGANIZER_ID);

        // Set up document snapshots to return these tournaments
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.getDocuments()).thenReturn(Arrays.asList(queryDocument1, queryDocument2));

        when(queryDocument1.exists()).thenReturn(true);
        when(queryDocument1.toObject(Tournament.class)).thenReturn(tournament1);
        when(queryDocument1.getId()).thenReturn("Tournament1_Id");

        when(queryDocument2.exists()).thenReturn(true);
        when(queryDocument2.toObject(Tournament.class)).thenReturn(tournament2);
        when(queryDocument2.getId()).thenReturn("Tournament2_Id");

        // Execute the method
        List<Tournament> result = tournamentService.getTournamentsByOrganizerId(ORGANIZER_ID);

        // Assertions
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Tournament1_Id", result.get(0).getId());
        assertEquals("Tournament 1", result.get(0).getName());
        assertEquals("Tournament2_Id", result.get(1).getId());
        assertEquals("Tournament 2", result.get(1).getName());

        // Verify interactions with Firestore
        verify(tournamentsCollection).whereEqualTo("organizerId", ORGANIZER_ID);
        verify(query).get();
        verify(querySnapshot).getDocuments();
    }

    @Test
    void testGetTournamentsByPlayerId() throws ExecutionException, InterruptedException {

        String USER_ID = "userId";

        // Mock data for tournaments
        Tournament tournament1 = new Tournament();
        tournament1.setId("Tournament1_Id");
        tournament1.setName("Tournament 1");
        tournament1.setUsers(Arrays.asList(USER_ID, "Player456"));

        Tournament tournament2 = new Tournament();
        tournament2.setId("Tournament2_Id");
        tournament2.setName("Tournament 2");
        tournament2.setUsers(Arrays.asList("Player789", USER_ID));

        // Set up document snapshots to return these tournaments
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.toObjects(Tournament.class)).thenReturn(Arrays.asList(tournament1, tournament2));

        // Execute the method
        List<Tournament> result = tournamentService.getTournamentsByPlayerId(USER_ID);

        // Assertions
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Tournament1_Id", result.get(0).getId());
        assertEquals("Tournament 1", result.get(0).getName());
        assertTrue(result.get(0).getUsers().contains(USER_ID));

        assertEquals("Tournament2_Id", result.get(1).getId());
        assertEquals("Tournament 2", result.get(1).getName());
        assertTrue(result.get(1).getUsers().contains(USER_ID));

        // Verify interactions with Firestore
        verify(tournamentsCollection).whereArrayContains("users", USER_ID);
        verify(query).get();
        verify(querySnapshot).toObjects(Tournament.class);
    }

    @Test
    void testGetTournamentsByPlayerId_NoTournaments() throws ExecutionException, InterruptedException {

        String USER_ID = "userId";

        // Mock a scenario where no tournaments are returned
        when(querySnapshotFuture.get()).thenReturn(querySnapshot);
        when(querySnapshot.toObjects(Tournament.class)).thenReturn(Arrays.asList());

        // Execute the method
        List<Tournament> result = tournamentService.getTournamentsByPlayerId(USER_ID);

        // Assertions
        assertNotNull(result);
        assertEquals(0, result.size(), "Expected no tournaments to be returned.");
    }

    @Test
    void testAddTournament() throws ExecutionException, InterruptedException {
        // Mock the inputs and expected behavior

        // Mock data for the tournament with all the attributes
        Tournament testTournament = new Tournament();
        tournament.setId("Tournament1_Id");
        tournament.setName("Summer Cup");
        tournament.setYear(2024);
        tournament.setVenue("Stadium A");
        tournament.setDateTime("2024-06-15T10:00:00");
        tournament.setNumPlayers(32);
        tournament.setStatus("Scheduled");
        tournament.setMinMMR(1500.0);
        tournament.setMaxMMR(2000.0);
        tournament.setRounds(new ArrayList<>());
        tournament.setOrganizerId("user123");  // Mock organizer ID
        tournament.setUsers(Arrays.asList("user123", "user456"));  // Mock user list

        String userId = "userId";  // Mock the user ID

        // Mock Firestore method calls
        when(firestore.collection("tournaments")).thenReturn(mock(CollectionReference.class)); // Mock collection call
        when(firestore.collection("tournaments").document()).thenReturn(docRef);  // Mock document call
        when(docRef.getId()).thenReturn("Tournament1_Id");  // Mock generated ID
        when(docRef.set(tournament)).thenReturn(writeResultFuture);  // Mock setting the document

        // Mock the write result future behavior
        when(writeResultFuture.get()).thenReturn(writeResult);  // Return mocked WriteResult when future is resolved

        // Execute the method
        String result = tournamentService.addTournament(testTournament, userId);

        // Assertions
        assertNotNull(result);
        assertTrue(result.contains("Tournament created successfully with ID: Tournament1_Id"));
        assertTrue(result.contains("at:"));  // Check that timestamp is included in the result

        // Verify that the Firestore methods were called correctly
        verify(firestore).collection("tournaments");  // Verify that the collection was accessed
        verify(docRef).getId();  // Verify that the document ID was generated
        verify(docRef).set(tournament);  // Verify that the set() method was called
    }

    @Test
    void testUpdateTournament() throws ExecutionException, InterruptedException {
        // Prepare mock data
        String tournamentId = "Tournament123";
        String organizerId = "organizer1";

        // Mock the ApiFuture<DocumentSnapshot> and DocumentSnapshot
        when(docRef.get()).thenReturn(documentSnapshotFuture);  // Mock docRef.get() to return the future

        // Mock the document and Firestore methods
        when(firestore.collection("tournaments")).thenReturn(mock(CollectionReference.class));
        when(firestore.collection("tournaments").document(tournamentId)).thenReturn(docRef);
        when(docRef.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);  // Simulating that the document exists
        when(documentSnapshot.getString("name")).thenReturn(tournament.getName());
        when(documentSnapshot.get("organizerId")).thenReturn(tournament.getOrganizerId());

        // Mock the userService (to simulate user role check)
        UserDTO user = new UserDTO();
        user.setId(organizerId);
        user.setRole("organizer");
        when(userService.getUser(organizerId)).thenReturn(user);  // Mock the response of getUser

        // Mock the update behavior
        when(docRef.update(anyMap())).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(writeResult);

        // Prepare tournament data to be updated
        Map<String, Object> tournamentData = new HashMap<>();
        tournamentData.put("status", "Completed");
        tournamentData.put("venue", "New Venue");

        // Execute the update method
        String result = tournamentService.updateTournament(tournamentId, organizerId, tournamentData);

        // Assertions
        assertNotNull(result);
        assertTrue(result.contains("Tournament with ID: Tournament123 updated successfully"));
        assertTrue(result.contains("at:"));  // Check if update time is included

        // Verify Firestore update call
        verify(docRef).update(anyMap());  // Ensure that the update method was called with the data

        // Verify userService call
        verify(userService).getUser(organizerId);  // Verify that getUser method was called with the organizerId
    }

    @Test
    void testUpdateTournamentThrowsTournamentNotFoundException() throws ExecutionException, InterruptedException {
        // Prepare mock data
        String tournamentId = "NonExistingTournamentId";  // Assume this ID does not exist
        String organizerId = "organizer1";
        Map<String, Object> tournamentData = new HashMap<>();
        tournamentData.put("status", "Completed");

        // Mock the Firestore behavior where the tournament is not found
        when(firestore.collection("tournaments")).thenReturn(mock(CollectionReference.class));
        when(firestore.collection("tournaments").document(tournamentId)).thenReturn(docRef);

        // Mock that the document does not exist
        when(docRef.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);  // Returns a mock document snapshot
        when(documentSnapshot.exists()).thenReturn(false);  // Simulate non-existing tournament

        // Execute the method and expect an exception
        TournamentNotFoundException exception = assertThrows(TournamentNotFoundException.class, () -> {
            tournamentService.updateTournament(tournamentId, organizerId, tournamentData);
        });

        // Assertions on the exception
        assertNotNull(exception);  // Ensure that the exception is not null
        assertEquals("Tournament with ID: NonExistingTournamentId not found", exception.getMessage());  // Check the exception message
    }

    @Test
    void testRegisterUser_TournamentNotFound() throws ExecutionException, InterruptedException {
        // Prepare mock data for a non-existing tournament
        String tournamentId = "NonExistingTournamentId";
        String userId = "user1";

        when(firestore.collection("tournaments")).thenReturn(mock(CollectionReference.class));
        when(firestore.collection("tournaments").document(tournamentId)).thenReturn(docRef);
        when(docRef.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(false);  // Tournament doesn't exist

        // Execute the method and expect an exception
        TournamentNotFoundException exception = assertThrows(TournamentNotFoundException.class, () -> {
            tournamentService.registerUser(tournamentId, userId);
        });

        // Assertions
        assertEquals("Tournament with ID: NonExistingTournamentId not found", exception.getMessage());
    }

    @Test
    void testRegisterUser_TournamentHasAlreadyStarted() throws ExecutionException, InterruptedException {
        // Prepare mock data for tournament
        String tournamentId = "Tournament1";
        String userId = "user1";
        Tournament testTournament = new Tournament();
        testTournament.setRounds(Collections.singletonList(new Round()));  // Simulate that the tournament has started

        when(firestore.collection("tournaments")).thenReturn(mock(CollectionReference.class));
        when(firestore.collection("tournaments").document(tournamentId)).thenReturn(docRef);
        when(docRef.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.get("users")).thenReturn(new ArrayList<String>());
        when(tournamentService.getTournament(tournamentId)).thenReturn(tournament);  // Mock getTournament

        // Execute the method
        String result = tournamentService.registerUser(tournamentId, userId);

        // Assertions
        assertEquals("Tournament's has already begin", result);
    }

    @Test
    void testRegisterUser_RegistrationDateOver() throws ExecutionException, InterruptedException {
        // Prepare mock data for tournament with past registration date
        String tournamentId = "Tournament1";
        String userId = "user1";
        Tournament testTournament = new Tournament();
        testTournament.setRounds(new ArrayList<>());
        when(tournament.checkDate(any())).thenReturn(false);  // Simulate that registration date is over

        when(firestore.collection("tournaments")).thenReturn(mock(CollectionReference.class));
        when(firestore.collection("tournaments").document(tournamentId)).thenReturn(docRef);
        when(docRef.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.get("users")).thenReturn(new ArrayList<String>());
        when(tournamentService.getTournament(tournamentId)).thenReturn(tournament);

        // Execute the method
        String result = tournamentService.registerUser(tournamentId, userId);

        // Assertions
        assertEquals("Tournament's registration date is already over!", result);
    }

    @Test
    void testRegisterUser_AlreadyRegistered() throws ExecutionException, InterruptedException {
        // Prepare mock data for tournament where user is already registered
        String tournamentId = "Tournament1";
        String userId = "user1";
        Tournament testTournament = new Tournament();
        testTournament.setRounds(new ArrayList<>());
        testTournament.setMinMMR(1000);
        testTournament.setMaxMMR(1500);

        List<String> currentUsers = new ArrayList<>();
        currentUsers.add(userId);  // User is already registered

        when(firestore.collection("tournaments")).thenReturn(mock(CollectionReference.class));
        when(firestore.collection("tournaments").document(tournamentId)).thenReturn(docRef);
        when(docRef.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.get("users")).thenReturn(currentUsers);
        when(tournamentService.getTournament(tournamentId)).thenReturn(tournament);

        // Execute the method
        String result = tournamentService.registerUser(tournamentId, userId);

        // Assertions
        assertEquals("You have already registered for this Tournament!", result);
    }

    @Test
    void testRegisterUser_SuccessfulRegistration() throws ExecutionException, InterruptedException {
        // Prepare mock data for successful registration
        String tournamentId = "Tournament1";
        String userId = "user1";
        Tournament testTournament = new Tournament();
        testTournament.setRounds(new ArrayList<>());
        testTournament.setMinMMR(1000);
        testTournament.setMaxMMR(1500);

        UserDTO user = mock(UserDTO.class);
        when(user.getPlayerDetails().calculateMMR()).thenReturn(1200.0); // Mock the getPlayerDetails method

        List<String> currentUsers = new ArrayList<>();

        when(firestore.collection("tournaments")).thenReturn(mock(CollectionReference.class));
        when(firestore.collection("tournaments").document(tournamentId)).thenReturn(docRef);
        when(docRef.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.get("users")).thenReturn(currentUsers);
        when(tournamentService.getTournament(tournamentId)).thenReturn(tournament);

        // Mock update success (return ApiFuture instead of WriteResult directly)
        when(docRef.update("users", currentUsers)).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(writeResult);

        // Execute the method
        String result = tournamentService.registerUser(tournamentId, userId);

        // Assertions
        assertTrue(result.contains("has successfully joined Tournament"));
    }

    @Test
    void testRegisterUser_UserNotEligible() throws ExecutionException, InterruptedException {
        // Prepare mock data for tournament and user eligibility check
        String tournamentId = "Tournament1";
        String userId = "user1";
        Tournament testTournament = new Tournament();
        testTournament.setRounds(new ArrayList<>());
        testTournament.setMinMMR(1000);
        testTournament.setMaxMMR(1500);

        UserDTO user = mock(UserDTO.class);
        when(user.getPlayerDetails().calculateMMR()).thenReturn(1600.0); // Mock the getPlayerDetails method

        when(firestore.collection("tournaments")).thenReturn(mock(CollectionReference.class));
        when(firestore.collection("tournaments").document(tournamentId)).thenReturn(docRef);
        when(docRef.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.get("users")).thenReturn(new ArrayList<String>());
        when(tournamentService.getTournament(tournamentId)).thenReturn(tournament);

        // Execute the method
        String result = tournamentService.registerUser(tournamentId, userId);

        // Assertions
        assertTrue(result.contains("Player with MMR"));
    }

    @Test
    void testCancelRegistration_SuccessfulUnregistration() throws ExecutionException, InterruptedException {
        // Prepare mock data for successful unregistration
        String tournamentId = "Tournament1";
        String userId = "user1";
        List<String> registeredUsers = new ArrayList<>(Arrays.asList("user1", "user2"));

        // Mock document exists check and registered users data
        when(docRef.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.get("users")).thenReturn(registeredUsers);

        // Mock the update method (when updating "users")
        when(docRef.update("users", registeredUsers)).thenReturn(writeResultFuture);
        when(writeResultFuture.get()).thenReturn(writeResult);

        // Create an instance of the service you are testing
        TournamentService testTournamentService = new TournamentService();

        // Execute the method
        String result = testTournamentService.cancelRegistration(tournamentId, userId);

        // Assertions
        assertTrue(result.contains("successfully unregistered"));
        assertFalse(registeredUsers.contains(userId));  // Verify user was removed from the list
    }

    @Test
    void testCancelRegistration_UserNotRegistered() throws ExecutionException, InterruptedException {
        // Prepare mock data where the user is not registered
        String tournamentId = "Tournament1";
        String userId = "user3"; // User not in the list
        List<String> registeredUsers = new ArrayList<>(Arrays.asList("user1", "user2"));

        // Mock document exists check and registered users data
        when(docRef.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenReturn(documentSnapshot);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.get("users")).thenReturn(registeredUsers);

        // Create an instance of the service you are testing
        TournamentService testTournamentService = new TournamentService();

        // Execute the method
        String result = testTournamentService.cancelRegistration(tournamentId, userId);

        // Assertions
        assertEquals("You are not registered to this event!", result); // Expecting specific error message
    }

    @Test
    void testCancelRegistration_TournamentNotFound() throws ExecutionException, InterruptedException {
        // Prepare mock data for tournament not found scenario
        String tournamentId = "Tournament1";
        String userId = "user1";

        // Create a mock document reference and simulate tournament not found
        when(docRef.get()).thenReturn(documentSnapshotFuture);
        when(documentSnapshotFuture.get()).thenThrow(new ExecutionException(new TournamentNotFoundException(tournamentId)));

        // Create an instance of the service you are testing
        TournamentService testTournamentService = new TournamentService();

        // Execute the method and assert exception
        TournamentNotFoundException exception = assertThrows(TournamentNotFoundException.class, () -> testTournamentService.cancelRegistration(tournamentId, userId));

        // You can also assert the message or details of the exception
        assertEquals("Tournament with ID: " + tournamentId + " not found", exception.getMessage());
    }

}
