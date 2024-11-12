package com.projectshowdown.service;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.projectshowdown.dto.UserDTO;
import com.projectshowdown.entities.User;
import com.projectshowdown.exceptions.PlayerNotFoundException;
import com.google.cloud.firestore.WriteResult;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private Firestore firestore;

    @Mock
    private ApiFuture<QuerySnapshot> querySnapshotApiFuture;

    @Mock
    private WriteResult writeResult;

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

    @Test
    public void testDeletePlayer_PlayerExists() throws ExecutionException, InterruptedException {
        // Mock Firestore behavior
        when(firestore.collection("users").document("existingUserId")).thenReturn(documentReference);

        // Mock document existence
        DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
        when(documentReference.get()).thenReturn(ApiFutures.immediateFuture(documentSnapshot));
        when(documentSnapshot.exists()).thenReturn(true);

        // Mock delete result
        when(documentReference.delete()).thenReturn(ApiFutures.immediateFuture(writeResult));

        // Call the method and verify the result
        String result = userService.deletePlayer("existingUserId");
        assertTrue(result.contains("Player with the id:existingUserId successfully deleted"));
    }

    @Test
    public void testDeletePlayer_PlayerDoesNotExist() {
        // Mock Firestore behavior
        when(firestore.collection("users").document("nonExistentUserId")).thenReturn(documentReference);

        // Mock document non-existence
        DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
        when(documentReference.get()).thenReturn(ApiFutures.immediateFuture(documentSnapshot));
        when(documentSnapshot.exists()).thenReturn(false);

        // Verify that PlayerNotFoundException is thrown
        PlayerNotFoundException exception = assertThrows(PlayerNotFoundException.class, () -> {
            userService.deletePlayer("nonExistentUserId");
        });

        // Optional: Verify the exception message, if desired
        assertEquals("Player with ID nonExistentUserId not found", exception.getMessage());

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateUser_SuccessfulUpdate() throws ExecutionException, InterruptedException {
        // Mock Firestore behavior for an existing user
        when(firestore.collection("users").document("existingUserId")).thenReturn(documentReference);

        DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
        when(documentReference.get()).thenReturn(ApiFutures.immediateFuture(documentSnapshot));
        when(documentSnapshot.exists()).thenReturn(true);

        when(documentReference.update(any(Map.class))).thenReturn(ApiFutures.immediateFuture(writeResult));

        // Call the updateUser method with valid data
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", "newUsername");
        String result = userService.updateUser("existingUserId", userData);

        // Verify success message
        assertTrue(result.contains("User with ID: existingUserId updated successfully"));
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        // Mock Firestore behavior for non-existing user
        when(firestore.collection("users").document("nonExistentUserId")).thenReturn(documentReference);

        DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
        when(documentReference.get()).thenReturn(ApiFutures.immediateFuture(documentSnapshot));
        when(documentSnapshot.exists()).thenReturn(false);

        // Verify that PlayerNotFoundException is thrown
        PlayerNotFoundException exception = assertThrows(PlayerNotFoundException.class, () -> {
            userService.updateUser("nonExistentUserId", new HashMap<>());
        });

        assertEquals("User with ID: nonExistentUserId does not exist.", exception.getMessage());
    }

    @Test
    public void testUpdateUser_DuplicateEmail() throws ExecutionException, InterruptedException {
        // Mock email check method to return true (indicating duplicate email)
        when(userService.checkEmailExists("duplicate@example.com")).thenReturn(true);

        // Attempt to update user with duplicate email
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", "duplicate@example.com");
        String result = userService.updateUser("existingUserId", userData);

        // Verify the duplicate email message
        assertEquals("A user account with the email duplicate@example.com already exists!", result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateUser_PasswordIsHashed() throws ExecutionException, InterruptedException {
        // Mock Firestore behavior for an existing user
        when(firestore.collection("users").document("existingUserId")).thenReturn(documentReference);

        DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
        when(documentReference.get()).thenReturn(ApiFutures.immediateFuture(documentSnapshot));
        when(documentSnapshot.exists()).thenReturn(true);

        when(documentReference.update(any(Map.class))).thenReturn(ApiFutures.immediateFuture(writeResult));

        // Prepare user data with a password
        Map<String, Object> userData = new HashMap<>();
        userData.put("password", "plainPassword");

        // Call updateUser
        userService.updateUser("existingUserId", userData);

        // Capture and check that password is hashed before update
        Mockito.verify(documentReference).update(Mockito.argThat(argument -> {
            String hashedPassword = (String) argument.get("password");
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            return passwordEncoder.matches("plainPassword", hashedPassword);
        }));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateUser_OrganizerDetailsVerifiedFieldSetToFalse() throws ExecutionException, InterruptedException {
        // Mock Firestore behavior for an existing user
        when(firestore.collection("users").document("existingUserId")).thenReturn(documentReference);

        DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
        when(documentReference.get()).thenReturn(ApiFutures.immediateFuture(documentSnapshot));
        when(documentSnapshot.exists()).thenReturn(true);

        when(documentReference.update(any(Map.class))).thenReturn(ApiFutures.immediateFuture(writeResult));

        // Prepare user data with organizer details containing the "verified" field
        Map<String, Object> organizerDetails = new HashMap<>();
        organizerDetails.put("verified", true);
        Map<String, Object> userData = new HashMap<>();
        userData.put("organizerDetails", organizerDetails);

        // Call updateUser
        userService.updateUser("existingUserId", userData);

        // Capture and verify that "verified" field is set to false
        Mockito.verify(documentReference).update(Mockito.argThat(argument -> {
            Map<String, Object> updatedOrganizerDetails = (Map<String, Object>) argument.get("organizerDetails");
            return updatedOrganizerDetails != null && updatedOrganizerDetails.get("verified").equals(false);
        }));
    }
}
