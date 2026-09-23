package com.example.organshare.repositories;

import com.example.organshare.firebase.FirestoreCollections;
import com.example.organshare.firebase.FirestoreHelper;
import com.example.organshare.models.AuditLog;
import com.example.organshare.models.UserModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminRepository {
    private final FirebaseFirestore db;

    public interface DataCallback<T> {
        void onSuccess(T result);
        void onFailure(String error);
    }

    public AdminRepository() {
        this.db = FirestoreHelper.getFirestore();
    }

    public void getAllUsers(DataCallback<List<UserModel>> callback) {
        db.collection(FirestoreCollections.USERS)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<UserModel> list = new ArrayList<>();
                    for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                        list.add(snap.toObject(UserModel.class));
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void updateUserVerification(String userId, boolean verified, DataCallback<Void> callback) {
        db.collection(FirestoreCollections.USERS).document(userId)
                .update("verified", verified)
                .addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void updateUserSuspension(String userId, boolean suspended, DataCallback<Void> callback) {
        db.collection(FirestoreCollections.USERS).document(userId)
                .update("suspended", suspended)
                .addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getAuditLogs(DataCallback<List<AuditLog>> callback) {
        db.collection(FirestoreCollections.AUDIT_LOGS)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<AuditLog> list = new ArrayList<>();
                    for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                        list.add(snap.toObject(AuditLog.class));
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getDashboardMetrics(DataCallback<Map<String, Integer>> callback) {
        final Map<String, Integer> metrics = new HashMap<>();
        
        db.collection(FirestoreCollections.DONORS).get().addOnSuccessListener(donors -> {
            metrics.put("donorsCount", donors.size());
            db.collection(FirestoreCollections.HOSPITALS).get().addOnSuccessListener(hospitals -> {
                metrics.put("hospitalsCount", hospitals.size());
                db.collection(FirestoreCollections.ORGAN_REQUESTS).get().addOnSuccessListener(requests -> {
                    metrics.put("requestsCount", requests.size());
                    db.collection(FirestoreCollections.INVENTORY).get().addOnSuccessListener(inventory -> {
                        metrics.put("inventoryCount", inventory.size());
                        db.collection(FirestoreCollections.DELIVERIES).get().addOnSuccessListener(deliveries -> {
                            metrics.put("deliveriesCount", deliveries.size());
                            callback.onSuccess(metrics);
                        }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                    }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
            }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
        }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}
