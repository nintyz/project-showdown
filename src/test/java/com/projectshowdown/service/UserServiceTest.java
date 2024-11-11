package com.projectshowdown.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.entities.User;
import com.projectshowdown.exceptions.PlayerNotFoundException;
import com.projectshowdown.util.DateTimeUtils;
import com.google.api.core.ApiFutures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private Firestore firestore;

    @Mock
    private ApiFuture<QuerySnapshot> querySnapshotApiFuture;

    @Mock
    private ApiFuture<DocumentSnapshot> documentSnapshotApiFuture;

    @Mock
    private DocumentReference documentReference;

    @InjectMocks
    private UserService userService;

    private User mockUser;
    private UserDTO mockUserDTO;

    @BeforeEach
    public void setUp() {
        mockUser = new User();
        mockUser.setEmail("testuser@example.com");
        mockUser.setPassword("hashedpassword");

        mockUserDTO = new UserDTO();
        mockUserDTO.setEmail("testuser@example.com");
        mockUserDTO.setRole("player");
    }

    @Test
    public void testCreateUser() throws ExecutionException, InterruptedException {
        // Mock Firestore behavior
        when(firestore.collection("users").whereEqualTo("email", "testuser@example.com").get())
                .thenReturn(querySnapshotApiFuture);
        when(querySnapshotApiFuture.get()).thenReturn(mock(QuerySnapshot.class));

        when(firestore.collection("users").document()).thenReturn(documentReference);
        when(documentReference.getId()).thenReturn("generatedId");

        // Use ApiFutures.immediateFuture instead of CompletableFuture
        when(documentReference.set(any(UserDTO.class))).thenReturn(ApiFutures.immediateFuture(null));

        // Call the method
        String result = userService.createUser(mockUser);

        // Verify that the result is as expected
        assert (result.contains("User created successfully with ID: generatedId"));
    }

    @Test
    public void testGetUserIdByEmail_UserFound() throws ExecutionException, InterruptedException {
        // Mock Firestore behavior
        QuerySnapshot querySnapshotMock = mock(QuerySnapshot.class);
        List<QueryDocumentSnapshot> documents = new ArrayList<>();
        QueryDocumentSnapshot documentSnapshot = mock(QueryDocumentSnapshot.class);
        when(documentSnapshot.getId()).thenReturn("userId");
        documents.add(documentSnapshot);

        when(querySnapshotApiFuture.get()).thenReturn(querySnapshotMock);
        when(querySnapshotMock.getDocuments()).thenReturn(documents);

        // Call the method
        String userId = userService.getUserIdByEmail("testuser@example.com");

        // Verify that the correct user ID is returned
        assertEquals("userId", userId);
    }

    @Test
    public void testGetAllPlayers() throws ExecutionException, InterruptedException {
        // Mock Firestore behavior
        QuerySnapshot querySnapshotMock = mock(QuerySnapshot.class);
        when(firestore.collection("users").whereEqualTo("role", "player").get())
                .thenReturn(querySnapshotApiFuture);

        List<QueryDocumentSnapshot> documents = new ArrayList<>();
        QueryDocumentSnapshot documentSnapshot = mock(QueryDocumentSnapshot.class);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(UserDTO.class)).thenReturn(mockUserDTO);
        documents.add(documentSnapshot);

        when(querySnapshotApiFuture.get()).thenReturn(querySnapshotMock);
        when(querySnapshotMock.getDocuments()).thenReturn(documents);

        // Call the method
        List<UserDTO> players = userService.getAllPlayers();

        // Verify that the list contains one player
        assertEquals(1, players.size());
        assertEquals("testuser@example.com", players.get(0).getEmail());
    }
}
