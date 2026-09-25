package com.example.organshare.repositories;

import com.example.organshare.firebase.FirestoreCollections;
import com.example.organshare.firebase.FirestoreHelper;
import com.example.organshare.models.Checkpoint;
import com.example.organshare.models.DeliveryModel;
import com.example.organshare.models.DeliveryPersonnelProfile;
import com.example.organshare.models.OrganRequest;
import com.example.organshare.utils.Constants;
import com.example.organshare.utils.DateTimeUtils;
import com.google.firebase.firestore.DocumentReference;
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

    public void getAllDeliveries(DataCallback<List<DeliveryModel>> callback) {
        db.collection(FirestoreCollections.DELIVERIES)
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

    public void updateDeliveryStatus(String deliveryId, String newStatus, DataCallback<Void> callback) {
        db.collection(FirestoreCollections.DELIVERIES).document(deliveryId)
                .update("currentStatus", newStatus, "lastUpdatedTime", DateTimeUtils.getCurrentDateTime())
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void addCheckpoint(String deliveryId, Checkpoint checkpoint, String newDeliveryStatus, DataCallback<Void> callback) {
        db.collection(FirestoreCollections.DELIVERIES).document(deliveryId)
                .update(
                        "checkpoints", FieldValue.arrayUnion(checkpoint),
                        "currentStatus", newDeliveryStatus,
                        "lastKnownLatitude", checkpoint.getLatitude(),
                        "lastKnownLongitude", checkpoint.getLongitude(),
                        "lastLocationName", checkpoint.getLocationName(),
                        "lastUpdatedTime", DateTimeUtils.getCurrentDateTime(),
                        "lastLocationUpdatedAt", DateTimeUtils.getCurrentDateTime()
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

    /**
     * Completes delivery, updates order, decrements request remaining quantity,
     * and transitions request status to FULFILLED (if complete) or PARTIALLY_FULFILLED.
     */
    public void markDeliveredAndComplete(
            final String deliveryId,
            final String orderId,
            final String requestId,
            final String completedBy,
            final DataCallback<Void> callback
    ) {
        final DocumentReference deliveryRef = db.collection(FirestoreCollections.DELIVERIES).document(deliveryId);

        deliveryRef.update(
                "currentStatus", Constants.DEL_COMPLETED,
                "deliveredAt", DateTimeUtils.getCurrentDateTime(),
                "completedAt", DateTimeUtils.getCurrentDateTime(),
                "completedBy", completedBy,
                "deliveryConfirmation", "CONFIRMED_AT_DESTINATION",
                "lastUpdatedTime", DateTimeUtils.getCurrentDateTime()
        ).addOnSuccessListener(aVoid -> {
            if (orderId != null && !orderId.isEmpty()) {
                db.collection(FirestoreCollections.ORDERS).document(orderId)
                        .update("orderStatus", "COMPLETED", "completionDate", DateTimeUtils.getCurrentDate());
            }

            if (requestId != null && !requestId.isEmpty()) {
                final DocumentReference reqRef = db.collection(FirestoreCollections.ORGAN_REQUESTS).document(requestId);
                reqRef.get().addOnSuccessListener(reqSnap -> {
                    if (reqSnap.exists()) {
                        OrganRequest req = reqSnap.toObject(OrganRequest.class);
                        if (req != null) {
                            int fulfilled = req.getFulfilledQuantity() + 1;
                            int totalReq = req.getRequestedQuantity();
                            int remaining = Math.max(0, totalReq - fulfilled);
                            String newReqStatus = (remaining == 0) ? Constants.STATUS_FULFILLED : Constants.STATUS_PARTIALLY_FULFILLED;

                            reqRef.update(
                                    "fulfilledQuantity", fulfilled,
                                    "remainingQuantity", remaining,
                                    "status", newReqStatus,
                                    "requestStatus", newReqStatus,
                                    "fulfilledAt", (remaining == 0) ? DateTimeUtils.getCurrentDateTime() : null
                            );
                        }
                    }
                    callback.onSuccess(null);
                }).addOnFailureListener(e -> callback.onSuccess(null));
            } else {
                callback.onSuccess(null);
            }
        }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}
