package com.projectshowdown.configs;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RandomDataService {

    public String addRandomData() {
        // Get Firestore instance
        Firestore dbFirestore = FirestoreClient.getFirestore();
        
        // Create a reference to the new collection (randomData)
        CollectionReference collectionRef = dbFirestore.collection("randomData");
        
        // Create random data to post
        Map<String, Object> data = new HashMap<>();
        data.put("field1", "randomValue1");
        data.put("field2", Math.random());  // Example of a random number
        data.put("timestamp", System.currentTimeMillis());
        
        // Add document to the collection
        ApiFuture<DocumentReference> future = collectionRef.add(data);
        
        try {
            // Get the document ID of the newly created document
            DocumentReference documentReference = future.get();
            return "Document added with ID: " + documentReference.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error adding document: " + e.getMessage();
        }
    }
}

