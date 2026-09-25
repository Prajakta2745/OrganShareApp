package com.example.organshare.repositories;

import com.example.organshare.firebase.FirestoreCollections;
import com.example.organshare.firebase.FirestoreHelper;
import com.example.organshare.models.AuditLog;
import com.example.organshare.models.DeliveryModel;
import com.example.organshare.models.OrganInventory;
import com.example.organshare.models.OrganRequest;
import com.example.organshare.models.UserModel;
import com.example.organshare.utils.Constants;
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

    /**
     * Dynamically computes complete Firebase statistics for Administrator Dashboard.
     */
    public void getDashboardMetrics(DataCallback<Map<String, Integer>> callback) {
        final Map<String, Integer> metrics = new HashMap<>();

        db.collection(FirestoreCollections.DONORS).get().addOnSuccessListener(donors -> {
            metrics.put("totalDonors", donors.size());
            db.collection(FirestoreCollections.HOSPITALS).get().addOnSuccessListener(hospitals -> {
                metrics.put("totalHospitals", hospitals.size());
                db.collection(FirestoreCollections.INVENTORY).get().addOnSuccessListener(inventorySnaps -> {
                    int availableOrgans = 0;
                    int allocatedOrgans = 0;
                    for (QueryDocumentSnapshot snap : inventorySnaps) {
                        OrganInventory inv = snap.toObject(OrganInventory.class);
                        if (Constants.INV_AVAILABLE.equalsIgnoreCase(inv.getAvailabilityStatus())) {
                            availableOrgans++;
                        } else if (Constants.INV_ALLOCATED.equalsIgnoreCase(inv.getAvailabilityStatus()) ||
                                   Constants.INV_IN_TRANSIT.equalsIgnoreCase(inv.getAvailabilityStatus())) {
                            allocatedOrgans++;
                        }
                    }
                    metrics.put("availableOrgans", availableOrgans);
                    metrics.put("allocatedOrgans", allocatedOrgans);
                    metrics.put("totalInventory", inventorySnaps.size());

                    db.collection(FirestoreCollections.ORGAN_REQUESTS).get().addOnSuccessListener(requestSnaps -> {
                        int pendingReq = 0;
                        int activeReq = 0;
                        int fulfilledReq = 0;
                        for (QueryDocumentSnapshot snap : requestSnaps) {
                            OrganRequest req = snap.toObject(OrganRequest.class);
                            String st = req.getStatus() != null ? req.getStatus() : "";
                            if (Constants.STATUS_PENDING.equalsIgnoreCase(st) || Constants.STATUS_UNDER_REVIEW.equalsIgnoreCase(st)) {
                                pendingReq++;
                            } else if (Constants.STATUS_APPROVED.equalsIgnoreCase(st) ||
                                       Constants.STATUS_ACTIVE.equalsIgnoreCase(st) ||
                                       Constants.STATUS_PARTIALLY_FULFILLED.equalsIgnoreCase(st) ||
                                       Constants.STATUS_ALLOCATED.equalsIgnoreCase(st) ||
                                       Constants.STATUS_IN_TRANSIT.equalsIgnoreCase(st)) {
                                activeReq++;
                            } else if (Constants.STATUS_FULFILLED.equalsIgnoreCase(st) || Constants.STATUS_COMPLETED.equalsIgnoreCase(st) || Constants.STATUS_DELIVERED.equalsIgnoreCase(st)) {
                                fulfilledReq++;
                            }
                        }
                        metrics.put("pendingRequests", pendingReq);
                        metrics.put("activeRequests", activeReq);
                        metrics.put("fulfilledRequests", fulfilledReq);
                        metrics.put("totalRequests", requestSnaps.size());

                        db.collection(FirestoreCollections.DELIVERIES).get().addOnSuccessListener(deliverySnaps -> {
                            int pendingDel = 0;
                            int activeDel = 0;
                            int delayedDel = 0;
                            int completedDel = 0;
                            for (QueryDocumentSnapshot snap : deliverySnaps) {
                                DeliveryModel del = snap.toObject(DeliveryModel.class);
                                String dst = del.getCurrentStatus() != null ? del.getCurrentStatus() : "";
                                if (Constants.DEL_ASSIGNED.equalsIgnoreCase(dst) || "PICKUP_PENDING".equalsIgnoreCase(dst)) {
                                    pendingDel++;
                                } else if (Constants.DEL_DELAYED.equalsIgnoreCase(dst)) {
                                    delayedDel++;
                                } else if (Constants.DEL_COMPLETED.equalsIgnoreCase(dst) || Constants.DEL_DELIVERED.equalsIgnoreCase(dst)) {
                                    completedDel++;
                                } else {
                                    activeDel++;
                                }
                            }
                            metrics.put("pendingDeliveries", pendingDel);
                            metrics.put("activeDeliveries", activeDel);
                            metrics.put("delayedDeliveries", delayedDel);
                            metrics.put("completedDeliveries", completedDel);
                            metrics.put("totalDeliveries", deliverySnaps.size());

                            callback.onSuccess(metrics);
                        }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                    }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
            }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
        }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}
