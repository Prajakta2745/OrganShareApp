package com.example.organshare.repositories;

import com.example.organshare.firebase.FirestoreCollections;
import com.example.organshare.firebase.FirestoreHelper;
import com.example.organshare.models.AppNotification;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NotificationRepository {
    private final FirebaseFirestore db;

    public interface DataCallback<T> {
        void onSuccess(T result);
        void onFailure(String error);
    }

    public NotificationRepository() {
        this.db = FirestoreHelper.getFirestore();
    }

    public void sendNotification(String recipientUserId, String recipientRole, String title, String message, String type, String refId, DataCallback<Void> callback) {
        String notifId = "NOTIF-" + UUID.randomUUID().toString().substring(0, 8);
        AppNotification notif = new AppNotification(notifId, recipientUserId, recipientRole, title, message, type, refId);

        db.collection(FirestoreCollections.NOTIFICATIONS).document(notifId)
                .set(notif)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e.getMessage());
                });
    }

    public void getNotificationsForUser(String userId, String userRole, DataCallback<List<AppNotification>> callback) {
        db.collection(FirestoreCollections.NOTIFICATIONS)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(30)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<AppNotification> list = new ArrayList<>();
                    for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                        AppNotification notif = snap.toObject(AppNotification.class);
                        if (userId.equals(notif.getRecipientUserId()) ||
                                "ALL".equalsIgnoreCase(notif.getRecipientRole()) ||
                                userRole.equalsIgnoreCase(notif.getRecipientRole())) {
                            list.add(notif);
                        }
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}
