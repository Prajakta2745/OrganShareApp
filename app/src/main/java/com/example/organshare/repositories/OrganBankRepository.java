package com.example.organshare.repositories;

import com.example.organshare.firebase.FirestoreCollections;
import com.example.organshare.firebase.FirestoreHelper;
import com.example.organshare.models.DeliveryModel;
import com.example.organshare.models.DeliveryPersonnelProfile;
import com.example.organshare.models.OrderModel;
import com.example.organshare.models.OrganBankProfile;
import com.example.organshare.models.OrganInventory;
import com.example.organshare.models.OrganRequest;
import com.example.organshare.utils.Constants;
import com.example.organshare.utils.DateTimeUtils;
import com.example.organshare.utils.IdGenerator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrganBankRepository {
    private final FirebaseFirestore db;

    public interface DataCallback<T> {
        void onSuccess(T result);
        void onFailure(String error);
    }

    public OrganBankRepository() {
        this.db = FirestoreHelper.getFirestore();
    }

    public void saveBankProfile(OrganBankProfile bankProfile, DataCallback<Void> callback) {
        db.collection(FirestoreCollections.ORGAN_BANKS)
                .document(bankProfile.getBankId())
                .set(bankProfile)
                .addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getBankByUserId(String userId, DataCallback<OrganBankProfile> callback) {
        db.collection(FirestoreCollections.ORGAN_BANKS)
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        OrganBankProfile profile = queryDocumentSnapshots.getDocuments().get(0).toObject(OrganBankProfile.class);
                        callback.onSuccess(profile);
                    } else {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getAllRequests(DataCallback<List<OrganRequest>> callback) {
        db.collection(FirestoreCollections.ORGAN_REQUESTS)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<OrganRequest> requests = new ArrayList<>();
                    for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                        requests.add(snap.toObject(OrganRequest.class));
                    }
                    callback.onSuccess(requests);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getInventory(DataCallback<List<OrganInventory>> callback) {
        db.collection(FirestoreCollections.INVENTORY)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<OrganInventory> items = new ArrayList<>();
                    for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                        items.add(snap.toObject(OrganInventory.class));
                    }
                    callback.onSuccess(items);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void saveInventoryItem(OrganInventory item, DataCallback<Void> callback) {
        db.collection(FirestoreCollections.INVENTORY)
                .document(item.getInventoryId())
                .set(item)
                .addOnSuccessListener(aVoid -> {
                    FirestoreHelper.logAction(item.getOrganBankId(), item.getOrganBankName(), Constants.ROLE_ORGAN_BANK,
                            "SAVE_INVENTORY", "INVENTORY", item.getInventoryId(),
                            "Updated inventory for " + item.getOrganType() + " (" + item.getBloodGroup() + ")");
                    callback.onSuccess(null);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void updateRequestStatus(String requestId, String newStatus, String adminOrBankId, String userName, DataCallback<Void> callback) {
        db.collection(FirestoreCollections.ORGAN_REQUESTS)
                .document(requestId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    FirestoreHelper.logAction(adminOrBankId, userName, Constants.ROLE_ORGAN_BANK,
                            "UPDATE_REQUEST_STATUS", "REQUEST", requestId, "Changed status to: " + newStatus);
                    callback.onSuccess(null);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getAvailableDeliveryPersonnel(DataCallback<List<DeliveryPersonnelProfile>> callback) {
        db.collection(FirestoreCollections.DELIVERY_PERSONNEL)
                .whereEqualTo("verified", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DeliveryPersonnelProfile> list = new ArrayList<>();
                    for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                        list.add(snap.toObject(DeliveryPersonnelProfile.class));
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void assignDeliveryPersonnel(OrderModel order, DeliveryPersonnelProfile personnel, DataCallback<String> callback) {
        String deliveryId = IdGenerator.generateDeliveryId();
        DeliveryModel delivery = new DeliveryModel();
        delivery.setDeliveryId(deliveryId);
        delivery.setOrderId(order.getOrderId());
        delivery.setRequestId(order.getRequestId());
        delivery.setOrganType(order.getOrganType());
        delivery.setBloodGroup(order.getBloodGroup());
        delivery.setDeliveryPersonId(personnel.getDeliveryPersonId());
        delivery.setDeliveryPersonName(personnel.getFullName());
        delivery.setDeliveryPersonPhone(personnel.getPhone());
        delivery.setPickupAddress(order.getPickupAddress());
        delivery.setPickupLatitude(order.getPickupLatitude());
        delivery.setPickupLongitude(order.getPickupLongitude());
        delivery.setDestinationHospitalName(order.getHospitalName());
        delivery.setDestinationAddress(order.getHospitalAddress());
        delivery.setDestinationLatitude(order.getHospitalLatitude());
        delivery.setDestinationLongitude(order.getHospitalLongitude());
        delivery.setCoordinatorContact(personnel.getPhone());
        delivery.setCurrentStatus(Constants.DEL_ASSIGNED);
        delivery.setLastUpdatedTime(DateTimeUtils.getCurrentDateTime());

        db.collection(FirestoreCollections.DELIVERIES).document(deliveryId)
                .set(delivery)
                .addOnSuccessListener(aVoid -> {
                    // Update order
                    db.collection(FirestoreCollections.ORDERS).document(order.getOrderId())
                            .update("deliveryPersonId", personnel.getDeliveryPersonId(),
                                    "deliveryPersonName", personnel.getFullName(),
                                    "deliveryPersonPhone", personnel.getPhone(),
                                    "deliveryId", deliveryId,
                                    "orderStatus", "ASSIGNED");

                    // Update request status to IN_TRANSIT
                    db.collection(FirestoreCollections.ORGAN_REQUESTS).document(order.getRequestId())
                            .update("status", Constants.STATUS_IN_TRANSIT);

                    // Update inventory status to IN_TRANSIT
                    if (order.getOrganInventoryId() != null) {
                        db.collection(FirestoreCollections.INVENTORY).document(order.getOrganInventoryId())
                                .update("availabilityStatus", Constants.INV_IN_TRANSIT);
                    }

                    callback.onSuccess(deliveryId);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}
