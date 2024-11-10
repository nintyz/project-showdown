// package com.projectshowdown.service;

// import com.google.api.core.ApiFuture;
// import com.google.cloud.firestore.DocumentReference;
// import com.google.cloud.firestore.DocumentSnapshot;
// import com.google.cloud.firestore.QuerySnapshot;
// import com.google.firebase.cloud.FirestoreClient;
// import com.projectshowdown.dto.UserDTO;
// import com.projectshowdown.entities.User;
// import com.projectshowdown.exceptions.PlayerNotFoundException;
// import com.projectshowdown.util.DateTimeUtils;
// import org.junit.Before;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Mockito;
// import org.mockito.junit.MockitoJUnitRunner;

// import java.util.concurrent.ExecutionException;
// import java.util.*;

// import static org.mockito.Mockito.*;

// @RunWith(MockitoJUnitRunner.class)
// public class UserServiceTest {

//     @Mock
//     private Firestore firestore;

//     @Mock
//     private ApiFuture<QuerySnapshot> querySnapshotApiFuture;

//     @Mock
//     private ApiFuture<DocumentSnapshot> documentSnapshotApiFuture;

//     @Mock
//     private DocumentReference documentReference;

//     @InjectMocks
//     private UserService userService;

//     private User mockUser;
//     private UserDTO mockUserDTO;

//     @Before
//     public void setUp() {
//         mockUser = new User();
//         mockUser.setEmail("testuser@example.com");
//         mockUser.setPassword("hashedpassword");

//         mockUserDTO = new UserDTO();
//         mockUserDTO.setEmail("testuser@example.com");
//         mockUserDTO.setRole("player");
//     }

//     @Test
//     public void testCreateUser() throws ExecutionException, InterruptedException {
//         // Mock Firestore behavior
//         when(firestore.collection("users").whereEqualTo("email", "testuser@example.com").get())
//             .thenReturn(querySnapshotApiFuture);
//         when(querySnapshotApiFuture.get()).thenReturn(mock(QuerySnapshot.class));
        
//         when(firestore.collection("users").document()).thenReturn(documentReference);
//         when(documentReference.getId()).thenReturn("generatedId");
//         when(documentReference.set(any(UserDTO.class))).thenReturn(ApiFuture.completedFuture(null));

//         // Call the method
//         String result = userService.createUser(mockUser);

//         // Verify that the result is as expected
//         assert(result.contains("User created successfully with ID: generatedId"));
//     }

//     @Test
//     public void testGetUserIdByEmail_UserFound() throws ExecutionException, InterruptedException {
//         // Mock Firestore behavior
//         when(firestore.collection("users").whereEqualTo("email", "testuser@example.com").get())
//             .thenReturn(querySnapshotApiFuture);
        
//         // Mock response from Firestore
//         List<QueryDocumentSnapshot> documents = new ArrayList<>();
//         QueryDocumentSnapshot documentSnapshot = mock(QueryDocumentSnapshot.class);
//         when(documentSnapshot.getId()).thenReturn("userId");
//         documents.add(documentSnapshot);
        
//         when(querySnapshotApiFuture.get()).thenReturn(mock(QuerySnapshot.class));
//         when(querySnapshotApiFuture.get().getDocuments()).thenReturn(documents);

//         // Call the method
//         String userId = userService.getUserIdByEmail("testuser@example.com");

//         // Verify that the correct user ID is returned
//         assert(userId.equals("userId"));
//     }

//     @Test(expected = PlayerNotFoundException.class)
//     public void testGetUserIdByEmail_UserNotFound() throws ExecutionException, InterruptedException {
//         // Mock Firestore behavior
//         when(firestore.collection("users").whereEqualTo("email", "nonexistent@example.com").get())
//             .thenReturn(querySnapshotApiFuture);
        
//         // Mock Firestore response as empty
//         when(querySnapshotApiFuture.get()).thenReturn(mock(QuerySnapshot.class));
//         when(querySnapshotApiFuture.get().getDocuments()).thenReturn(Collections.emptyList());

//         // Call the method expecting an exception
//         userService.getUserIdByEmail("nonexistent@example.com");
//     }

//     @Test
//     public void testUpdateUser() throws ExecutionException, InterruptedException {
//         // Mock Firestore behavior
//         String userId = "userId";
//         Map<String, Object> userData = new HashMap<>();
//         userData.put("email", "updatedemail@example.com");

//         DocumentReference docRef = mock(DocumentReference.class);
//         when(firestore.collection("users").document(userId)).thenReturn(docRef);
//         when(docRef.get()).thenReturn(documentSnapshotApiFuture);
//         DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
//         when(documentSnapshot.exists()).thenReturn(true);
//         when(documentSnapshotApiFuture.get()).thenReturn(documentSnapshot);

//         when(docRef.update(userData)).thenReturn(ApiFuture.completedFuture(null));

//         // Call the method
//         String result = userService.updateUser(userId, userData);

//         // Verify the result
//         assert(result.contains("User with ID: userId updated successfully"));
//     }

//     @Test(expected = PlayerNotFoundException.class)
//     public void testUpdateUser_UserNotFound() throws ExecutionException, InterruptedException {
//         // Mock Firestore behavior
//         String userId = "nonexistentUserId";
//         Map<String, Object> userData = new HashMap<>();
//         userData.put("email", "updatedemail@example.com");

//         DocumentReference docRef = mock(DocumentReference.class);
//         when(firestore.collection("users").document(userId)).thenReturn(docRef);
//         when(docRef.get()).thenReturn(documentSnapshotApiFuture);
//         DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
//         when(documentSnapshot.exists()).thenReturn(false);
//         when(documentSnapshotApiFuture.get()).thenReturn(documentSnapshot);

//         // Call the method expecting an exception
//         userService.updateUser(userId, userData);
//     }

//     @Test
//     public void testGetAllPlayers() throws ExecutionException, InterruptedException {
//         // Mock Firestore behavior
//         when(firestore.collection("users").whereEqualTo("role", "player").get())
//             .thenReturn(querySnapshotApiFuture);
        
//         // Mock response from Firestore
//         List<QueryDocumentSnapshot> documents = new ArrayList<>();
//         QueryDocumentSnapshot documentSnapshot = mock(QueryDocumentSnapshot.class);
//         when(documentSnapshot.exists()).thenReturn(true);
//         when(documentSnapshot.toObject(UserDTO.class)).thenReturn(mockUserDTO);
//         documents.add(documentSnapshot);
        
//         when(querySnapshotApiFuture.get()).thenReturn(mock(QuerySnapshot.class));
//         when(querySnapshotApiFuture.get().getDocuments()).thenReturn(documents);

//         // Call the method
//         List<UserDTO> players = userService.getAllPlayers();

//         // Verify that the list contains one player
//         assert(players.size() == 1);
//         assert(players.get(0).getEmail().equals("testuser@example.com"));
//     }
// }
