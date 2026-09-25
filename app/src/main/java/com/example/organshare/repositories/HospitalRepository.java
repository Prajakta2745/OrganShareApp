package com.example.organshare.repositories;

import com.example.organshare.firebase.FirestoreCollections;
import com.example.organshare.firebase.FirestoreHelper;
import com.example.organshare.models.HospitalProfile;
import com.example.organshare.models.OrderModel;
import com.example.organshare.models.OrganRequest;
import com.example.organshare.utils.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HospitalRepository {
    private final FirebaseFirestore db;

    public interface DataCallback<T> {
        void onSuccess(T result);
        void onFailure(String error);
    }

    public HospitalRepository() {
        this.db = FirestoreHelper.getFirestore();
    }

    public void saveHospitalProfile(HospitalProfile hospitalProfile, DataCallback<Void> callback) {
        db.collection(FirestoreCollections.HOSPITALS)
                .document(hospitalProfile.getHospitalId())
                .set(hospitalProfile)
                .addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getHospitalByUserId(String userId, DataCallback<HospitalProfile> callback) {
        db.collection(FirestoreCollections.HOSPITALS)
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        HospitalProfile profile = queryDocumentSnapshots.getDocuments().get(0).toObject(HospitalProfile.class);
                        callback.onSuccess(profile);
                    } else {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void createOrganRequest(OrganRequest request, DataCallback<Void> callback) {
        db.collection(FirestoreCollections.ORGAN_REQUESTS)
                .document(request.getRequestId())
                .set(request)
                .addOnSuccessListener(aVoid -> {
                    FirestoreHelper.logAction(request.getHospitalId(), request.getHospitalName(), Constants.ROLE_HOSPITAL,
                            "CREATE_ORGAN_REQUEST", "REQUEST", request.getRequestId(),
                            "Created " + request.getEmergencyLevel() + " request for " + request.getOrganRequired() + " (" + request.getBloodGroup() + ")");
                    callback.onSuccess(null);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getHospitalRequests(String hospitalId, DataCallback<List<OrganRequest>> callback) {
        // Query without forcing composite server index to prevent FAILED_PRECONDITION
        db.collection(FirestoreCollections.ORGAN_REQUESTS)
                .whereEqualTo("hospitalId", hospitalId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<OrganRequest> requests = new ArrayList<>();
                    for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                        requests.add(snap.toObject(OrganRequest.class));
                    }
                    // Robust client-side sort by createdAt descending
                    requests.sort((a, b) -> {
                        if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    });
                    callback.onSuccess(requests);
                })
                .addOnFailureListener(e -> {
                    // Fallback to fetch all and filter in memory if index or permission fails
                    db.collection(FirestoreCollections.ORGAN_REQUESTS).get().addOnSuccessListener(snaps -> {
                        List<OrganRequest> list = new ArrayList<>();
                        for (QueryDocumentSnapshot s : snaps) {
                            OrganRequest req = s.toObject(OrganRequest.class);
                            if (hospitalId != null && hospitalId.equalsIgnoreCase(req.getHospitalId())) {
                                list.add(req);
                            }
                        }
                        list.sort((a, b) -> {
                            if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
                            return b.getCreatedAt().compareTo(a.getCreatedAt());
                        });
                        callback.onSuccess(list);
                    }).addOnFailureListener(err -> callback.onFailure(err.getMessage()));
                });
    }

    public void getHospitalOrders(String hospitalId, DataCallback<List<OrderModel>> callback) {
        db.collection(FirestoreCollections.ORDERS)
                .whereEqualTo("hospitalId", hospitalId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<OrderModel> orders = new ArrayList<>();
                    for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                        orders.add(snap.toObject(OrderModel.class));
                    }
                    orders.sort((a, b) -> {
                        if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    });
                    callback.onSuccess(orders);
                })
                .addOnFailureListener(e -> {
                    db.collection(FirestoreCollections.ORDERS).get().addOnSuccessListener(snaps -> {
                        List<OrderModel> list = new ArrayList<>();
                        for (QueryDocumentSnapshot s : snaps) {
                            OrderModel ord = s.toObject(OrderModel.class);
                            if (hospitalId != null && hospitalId.equalsIgnoreCase(ord.getHospitalId())) {
                                list.add(ord);
                            }
                        }
                        list.sort((a, b) -> {
                            if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
                            return b.getCreatedAt().compareTo(a.getCreatedAt());
                        });
                        callback.onSuccess(list);
                    }).addOnFailureListener(err -> callback.onFailure(err.getMessage()));
                });
    }
}
