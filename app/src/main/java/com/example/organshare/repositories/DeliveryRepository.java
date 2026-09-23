package com.example.organshare.repositories;

import com.example.organshare.firebase.FirestoreCollections;
import com.example.organshare.firebase.FirestoreHelper;
import com.example.organshare.models.Checkpoint;
import com.example.organshare.models.DeliveryModel;
import com.example.organshare.models.DeliveryPersonnelProfile;
import com.example.organshare.utils.Constants;
import com.example.organshare.utils.DateTimeUtils;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DeliveryRepository {
    private final FirebaseFirestore db;

    public interface DataCallback<T> {
        void onSuccess(T result);
        void onFailure(String error);
    }

    public DeliveryRepository() {
        this.db = FirestoreHelper.getFirestore();
    }

    public void saveDeliveryPersonnelProfile(DeliveryPersonnelProfile profile, DataCallback<Void> callback) {
        db.collection(FirestoreCollections.DELIVERY_PERSONNEL)
                .document(profile.getDeliveryPersonId())
                .set(profile)
                .addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getDeliveryProfileByUserId(String userId, DataCallback<DeliveryPersonnelProfile> callback) {
        db.collection(FirestoreCollections.DELIVERY_PERSONNEL)
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DeliveryPersonnelProfile profile = queryDocumentSnapshots.getDocuments().get(0).toObject(DeliveryPersonnelProfile.class);
                        callback.onSuccess(profile);
                    } else {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getAssignedDeliveries(String deliveryPersonId, DataCallback<List<DeliveryModel>> callback) {
        db.collection(FirestoreCollections.DELIVERIES)
                .whereEqualTo("deliveryPersonId", deliveryPersonId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DeliveryModel> list = new ArrayList<>();
                    for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                        list.add(snap.toObject(DeliveryModel.class));
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getDeliveryById(String deliveryId, DataCallback<DeliveryModel> callback) {
        db.collection(FirestoreCollections.DELIVERIES).document(deliveryId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        callback.onSuccess(documentSnapshot.toObject(DeliveryModel.class));
                    } else {
                        callback.onFailure("Delivery record not found.");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void addCheckpoint(String deliveryId, Checkpoint checkpoint, String newDeliveryStatus, DataCallback<Void> callback) {
        db.collection(FirestoreCollections.DELIVERIES).document(deliveryId)
                .update(
                        "checkpoints", FieldValue.arrayUnion(checkpoint),
                        "currentStatus", newDeliveryStatus,
                        "lastKnownLatitude", checkpoint.getLatitude(),
                        "lastKnownLongitude", checkpoint.getLongitude(),
                        "lastUpdatedTime", DateTimeUtils.getCurrentDateTime()
                )
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void reportDelay(String deliveryId, String reason, DataCallback<Void> callback) {
        db.collection(FirestoreCollections.DELIVERIES).document(deliveryId)
                .update(
                        "currentStatus", Constants.DEL_DELAYED,
                        "delayReason", reason,
                        "lastUpdatedTime", DateTimeUtils.getCurrentDateTime()
                )
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void markDelivered(String deliveryId, String orderId, String requestId, DataCallback<Void> callback) {
        db.collection(FirestoreCollections.DELIVERIES).document(deliveryId)
                .update("currentStatus", Constants.DEL_DELIVERED, "lastUpdatedTime", DateTimeUtils.getCurrentDateTime())
                .addOnSuccessListener(aVoid -> {
                    if (orderId != null && !orderId.isEmpty()) {
                        db.collection(FirestoreCollections.ORDERS).document(orderId).update("orderStatus", "DELIVERED");
                    }
                    if (requestId != null && !requestId.isEmpty()) {
                        db.collection(FirestoreCollections.ORGAN_REQUESTS).document(requestId).update("status", Constants.STATUS_DELIVERED);
                    }
                    callback.onSuccess(null);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}
