package com.example.organshare.repositories;

import com.example.organshare.firebase.FirestoreCollections;
import com.example.organshare.firebase.FirestoreHelper;
import com.example.organshare.models.DonorProfile;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DonorRepository {
    private final FirebaseFirestore db;

    public interface DataCallback<T> {
        void onSuccess(T result);
        void onFailure(String error);
    }

    public DonorRepository() {
        this.db = FirestoreHelper.getFirestore();
    }

    public void saveDonorProfile(DonorProfile donorProfile, DataCallback<Void> callback) {
        db.collection(FirestoreCollections.DONORS)
                .document(donorProfile.getDonorId())
                .set(donorProfile)
                .addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getDonorByUserId(String userId, DataCallback<DonorProfile> callback) {
        db.collection(FirestoreCollections.DONORS)
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DonorProfile profile = queryDocumentSnapshots.getDocuments().get(0).toObject(DonorProfile.class);
                        callback.onSuccess(profile);
                    } else {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void searchDonors(String bloodGroup, String organ, String location, DataCallback<List<DonorProfile>> callback) {
        // Query verified donors
        db.collection(FirestoreCollections.DONORS)
                .whereEqualTo("verified", true)
                .limit(20)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DonorProfile> results = new ArrayList<>();
                    for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                        DonorProfile donor = snap.toObject(DonorProfile.class);
                        boolean matchesBlood = (bloodGroup == null || bloodGroup.isEmpty() || bloodGroup.equalsIgnoreCase(donor.getBloodGroup()));
                        boolean matchesOrgan = (organ == null || organ.isEmpty() || (donor.getOrgansWillingToDonate() != null && donor.getOrgansWillingToDonate().contains(organ)));
                        boolean matchesLocation = (location == null || location.isEmpty() ||
                                (donor.getCity() != null && donor.getCity().toLowerCase().contains(location.toLowerCase())) ||
                                (donor.getGeneralLocation() != null && donor.getGeneralLocation().toLowerCase().contains(location.toLowerCase())));

                        if (matchesBlood && matchesOrgan && matchesLocation) {
                            results.add(donor);
                        }
                    }
                    callback.onSuccess(results);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}
