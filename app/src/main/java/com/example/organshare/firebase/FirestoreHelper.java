package com.example.organshare.firebase;

import com.example.organshare.models.AuditLog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;

import java.util.UUID;

public class FirestoreHelper {
    private static FirebaseFirestore firestore;
    private static FirebaseAuth auth;
    private static FirebaseStorage storage;

    public static synchronized FirebaseFirestore getFirestore() {
        if (firestore == null) {
            firestore = FirebaseFirestore.getInstance();
            // Enable offline persistence
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();
            firestore.setFirestoreSettings(settings);
        }
        return firestore;
    }

    public static synchronized FirebaseAuth getAuth() {
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    public static synchronized FirebaseStorage getStorage() {
        if (storage == null) {
            storage = FirebaseStorage.getInstance();
        }
        return storage;
    }

    public static void logAction(String userId, String userName, String userRole, String action, String targetEntityType, String targetEntityId, String details) {
        String logId = "LOG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        AuditLog log = new AuditLog(logId, userId, userName, userRole, action, targetEntityType, targetEntityId, details);
        getFirestore().collection(FirestoreCollections.AUDIT_LOGS)
                .document(logId)
                .set(log);
    }
}
