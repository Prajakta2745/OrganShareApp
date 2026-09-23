package com.example.organshare.firebase;

import com.example.organshare.models.Allocation;
import com.example.organshare.models.AppNotification;
import com.example.organshare.models.DeliveryModel;
import com.example.organshare.models.OrderModel;
import com.example.organshare.models.OrganInventory;
import com.example.organshare.models.OrganRequest;
import com.example.organshare.utils.Constants;
import com.example.organshare.utils.DateTimeUtils;
import com.example.organshare.utils.IdGenerator;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

public class FirestoreTransactions {

    public interface AllocationCallback {
        void onSuccess(String orderId, String allocationId);
        void onFailure(String errorMessage);
    }

    /**
     * Atomically allocates an available inventory organ to a pending organ request.
     * Prevents race conditions and double-allocations if multiple coordinators or requests attempt
     * to lock the same organ concurrently.
     */
    public static void executeOrganAllocation(
            final String inventoryId,
            final String requestId,
            final String coordinatorId,
            final String coordinatorName,
            final String organBankId,
            final String organBankName,
            final String pickupAddress,
            final double pickupLat,
            final double pickupLng,
            final AllocationCallback callback
    ) {
        final FirebaseFirestore db = FirestoreHelper.getFirestore();
        final DocumentReference inventoryRef = db.collection(FirestoreCollections.INVENTORY).document(inventoryId);
        final DocumentReference requestRef = db.collection(FirestoreCollections.ORGAN_REQUESTS).document(requestId);

        final String allocationId = IdGenerator.generateAllocationId();
        final String orderId = IdGenerator.generateOrderId();
        final DocumentReference allocationRef = db.collection(FirestoreCollections.ALLOCATIONS).document(allocationId);
        final DocumentReference orderRef = db.collection(FirestoreCollections.ORDERS).document(orderId);

        db.runTransaction((Transaction.Function<Void>) transaction -> {
            // 1. Read current inventory doc
            DocumentSnapshot inventorySnap = transaction.get(inventoryRef);
            if (!inventorySnap.exists()) {
                throw new FirebaseFirestoreException("Organ inventory item does not exist.",
                        FirebaseFirestoreException.Code.NOT_FOUND);
            }

            OrganInventory inventory = inventorySnap.toObject(OrganInventory.class);
            if (inventory == null || !Constants.INV_AVAILABLE.equalsIgnoreCase(inventory.getAvailabilityStatus())) {
                throw new FirebaseFirestoreException("Organ is no longer available. It may have already been allocated.",
                        FirebaseFirestoreException.Code.ABORTED);
            }

            // 2. Read current request doc
            DocumentSnapshot requestSnap = transaction.get(requestRef);
            if (!requestSnap.exists()) {
                throw new FirebaseFirestoreException("Organ request not found.",
                        FirebaseFirestoreException.Code.NOT_FOUND);
            }

            OrganRequest request = requestSnap.toObject(OrganRequest.class);
            if (request == null || Constants.STATUS_ALLOCATED.equalsIgnoreCase(request.getStatus())) {
                throw new FirebaseFirestoreException("Organ request is already allocated or fulfilled.",
                        FirebaseFirestoreException.Code.ABORTED);
            }

            // 3. Update Inventory Status to ALLOCATED
            transaction.update(inventoryRef, "availabilityStatus", Constants.INV_ALLOCATED);

            // 4. Update Request Status to ALLOCATED and attach IDs
            transaction.update(requestRef, "status", Constants.STATUS_ALLOCATED);
            transaction.update(requestRef, "matchedInventoryId", inventoryId);
            transaction.update(requestRef, "allocatedOrderId", orderId);

            // 5. Create Allocation Document
            Allocation allocation = new Allocation();
            allocation.setAllocationId(allocationId);
            allocation.setRequestId(requestId);
            allocation.setHospitalId(request.getHospitalId());
            allocation.setHospitalName(request.getHospitalName());
            allocation.setInventoryId(inventoryId);
            allocation.setOrganType(inventory.getOrganType());
            allocation.setBloodGroup(inventory.getBloodGroup());
            allocation.setCoordinatorId(coordinatorId);
            allocation.setCoordinatorName(coordinatorName);
            allocation.setOrganBankId(organBankId);
            allocation.setOrganBankName(organBankName);
            allocation.setAllocationDate(DateTimeUtils.getCurrentDate());
            allocation.setAllocationTime(DateTimeUtils.getCurrentTime());
            allocation.setOrderId(orderId);
            allocation.setStatus(Constants.STATUS_ALLOCATED);
            transaction.set(allocationRef, allocation);

            // 6. Create Order Document
            OrderModel order = new OrderModel();
            order.setOrderId(orderId);
            order.setRequestId(requestId);
            order.setAllocationId(allocationId);
            order.setHospitalId(request.getHospitalId());
            order.setHospitalName(request.getHospitalName());
            order.setHospitalAddress(request.getHospitalAddress());
            order.setHospitalContact(request.getHospitalContact());
            order.setOrganBankId(organBankId);
            order.setOrganBankName(organBankName);
            order.setPickupAddress(pickupAddress);
            order.setPickupLatitude(pickupLat);
            order.setPickupLongitude(pickupLng);
            order.setOrganInventoryId(inventoryId);
            order.setOrganType(inventory.getOrganType());
            order.setBloodGroup(inventory.getBloodGroup());
            order.setOrderDate(DateTimeUtils.getCurrentDate());
            order.setOrderTime(DateTimeUtils.getCurrentTime());
            order.setOrderStatus("PENDING_ASSIGNMENT");
            transaction.set(orderRef, order);

            return null;
        }).addOnSuccessListener(aVoid -> {
            // Post audit log
            FirestoreHelper.logAction(coordinatorId, coordinatorName, Constants.ROLE_ORGAN_BANK,
                    "ALLOCATED_ORGAN", "INVENTORY", inventoryId,
                    "Allocated " + inventoryId + " to " + requestId + " (Order: " + orderId + ")");

            callback.onSuccess(orderId, allocationId);
        }).addOnFailureListener(e -> {
            callback.onFailure(e.getMessage());
        });
    }
}
