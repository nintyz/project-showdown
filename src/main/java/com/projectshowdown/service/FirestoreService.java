package com.projectshowdown.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.WriteBatch;
import com.google.firebase.cloud.FirestoreClient;

public class FirestoreService {
    private Firestore db;

    public FirestoreService() {
       this.db = FirestoreClient.getFirestore();
    }

    // Basic CRUD Operations
    public DocumentSnapshot getDocument(String collection, String documentId) throws ExecutionException, InterruptedException {
        return db.collection(collection)
                .document(documentId)
                .get()
                .get();
    }

    public String createDocument(String collection, Object data) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection(collection).document();
        String id = docRef.getId();
        docRef.set(data).get();
        return id;
    }

    public void updateDocument(String collection, String documentId, Map<String, Object> updates) 
            throws ExecutionException, InterruptedException {
        db.collection(collection)
                .document(documentId)
                .update(updates)
                .get();
    }

    public void deleteDocument(String collection, String documentId) throws ExecutionException, InterruptedException {
        db.collection(collection)
                .document(documentId)
                .delete()
                .get();
    }
    
    // Query Operations
    public List<QueryDocumentSnapshot> queryCollection(String collection, String field, Object value) 
            throws ExecutionException, InterruptedException {
        return db.collection(collection)
                .whereEqualTo(field, value)
                .get()
                .get()
                .getDocuments();
    }

    public List<QueryDocumentSnapshot> getAllDocuments(String collection) throws ExecutionException, InterruptedException {
        return db.collection(collection)
                .get()
                .get()
                .getDocuments();
    }

    // Existence checks
    public boolean documentExists(String collection, String documentId) throws ExecutionException, InterruptedException {
        return getDocument(collection, documentId).exists();
    }

    public boolean exists(String collection, String field, Object value) throws ExecutionException, InterruptedException {
        return !queryCollection(collection, field, value).isEmpty();
    }

    // Batch operations
    public void batchWrite(List<WriteOperation> operations) throws ExecutionException, InterruptedException {
        WriteBatch batch = db.batch();
        
        for (WriteOperation op : operations) {
            DocumentReference docRef = db.collection(op.getCollection()).document(op.getDocumentId());
            switch (op.getType()) {
                case CREATE:
                    batch.set(docRef, op.getData());
                    break;
                case UPDATE:
                    batch.update(docRef, op.getUpdates());
                    break;
                case DELETE:
                    batch.delete(docRef);
                    break;
            }
        }
        
        batch.commit().get();
    }

    // Helper class for batch operations
    public static class WriteOperation {
        private final OperationType type;
        private final String collection;
        private final String documentId;
        private final Object data;
        private final Map<String, Object> updates;

        public WriteOperation(OperationType type, String collection, String documentId, Object data, Map<String, Object> updates) {
            this.type = type;
            this.collection = collection;
            this.documentId = documentId;
            this.data = data;
            this.updates = updates;
        }

        // Getters
        public OperationType getType() { return type; }
        public String getCollection() { return collection; }
        public String getDocumentId() { return documentId; }
        public Object getData() { return data; }
        public Map<String, Object> getUpdates() { return updates; }
    }

    public enum OperationType {
        CREATE,
        UPDATE,
        DELETE
    }
}
