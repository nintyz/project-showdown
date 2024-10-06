package com.projectshowdown.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    public CustomUserDetailsService() {
        super();
        
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection("users").whereEqualTo("email", email).get();

        try {
            QuerySnapshot querySnapshot = future.get();
            if (!querySnapshot.isEmpty()) {
                // Assuming there is only one user with the given username
                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                String password = document.getString("password"); // Assuming password is stored
                return org.springframework.security.core.userdetails.User.withUsername(email)
                        .password(password) // Hashed password
                        .roles("ADMIN") // You can fetch roles from Firestore if needed
                        .build();
            } else {
                throw new UsernameNotFoundException("User not found: " + email);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new UsernameNotFoundException("Error retrieving user data", e);
        }
    }
}
