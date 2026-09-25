package com.example.organshare.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeliveryModel implements Serializable {
    private String deliveryId; // DEL-2026-XXXXX
    private String orderId;
    private String requestId;
    private String organType;
    private String bloodGroup;
    private int quantity = 1;
    private String deliveryPersonId;
    private String deliveryPersonName;
    private String deliveryPersonPhone;
    private String pickupAddress;
    private double pickupLatitude;
    private double pickupLongitude;
    private String destinationHospitalName;
    private String destinationAddress;
    private double destinationLatitude;
    private double destinationLongitude;
    private String coordinatorContact;
    private String currentStatus; // ASSIGNED, PICKUP_PENDING, PICKED_UP, IN_TRANSIT, CHECKPOINT_1, CHECKPOINT_2, NEAR_DESTINATION, DELIVERED, COMPLETED, DELAYED
    private String delayReason;
    private String priority; // EMERGENCY, URGENT, NORMAL
    private String requiredDate;
    private String requiredTime;
    private String expectedDeliveryTime;
    private String notes;
    private double lastKnownLatitude;
    private double lastKnownLongitude;
    private String lastLocationName;
    private String lastUpdatedTime;
    private String lastLocationUpdatedAt;
    private String estimatedArrival;
    private String assignedAt;
    private String pickedUpAt;
    private String deliveredAt;
    private String completedAt;
    private String completedBy;
    private String deliveryConfirmation;
    private List<Checkpoint> checkpoints = new ArrayList<>();

    @ServerTimestamp
    private Date createdAt;
    @ServerTimestamp
    private Date updatedAt;

    public DeliveryModel() {}

    public String getDeliveryId() { return deliveryId; }
    public void setDeliveryId(String deliveryId) { this.deliveryId = deliveryId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getOrganType() { return organType; }
    public void setOrganType(String organType) { this.organType = organType; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getDeliveryPersonId() { return deliveryPersonId; }
    public void setDeliveryPersonId(String deliveryPersonId) { this.deliveryPersonId = deliveryPersonId; }

    public String getDeliveryPersonName() { return deliveryPersonName; }
    public void setDeliveryPersonName(String deliveryPersonName) { this.deliveryPersonName = deliveryPersonName; }

    public String getDeliveryPersonPhone() { return deliveryPersonPhone; }
    public void setDeliveryPersonPhone(String deliveryPersonPhone) { this.deliveryPersonPhone = deliveryPersonPhone; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    public double getPickupLatitude() { return pickupLatitude; }
    public void setPickupLatitude(double pickupLatitude) { this.pickupLatitude = pickupLatitude; }

    public double getPickupLongitude() { return pickupLongitude; }
    public void setPickupLongitude(double pickupLongitude) { this.pickupLongitude = pickupLongitude; }

    public String getDestinationHospitalName() { return destinationHospitalName; }
    public void setDestinationHospitalName(String destinationHospitalName) { this.destinationHospitalName = destinationHospitalName; }

    public String getDestinationAddress() { return destinationAddress; }
    public void setDestinationAddress(String destinationAddress) { this.destinationAddress = destinationAddress; }

    public double getDestinationLatitude() { return destinationLatitude; }
    public void setDestinationLatitude(double destinationLatitude) { this.destinationLatitude = destinationLatitude; }

    public double getDestinationLongitude() { return destinationLongitude; }
    public void setDestinationLongitude(double destinationLongitude) { this.destinationLongitude = destinationLongitude; }

    public String getCoordinatorContact() { return coordinatorContact; }
    public void setCoordinatorContact(String coordinatorContact) { this.coordinatorContact = coordinatorContact; }

    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }

    public String getDelayReason() { return delayReason; }
    public void setDelayReason(String delayReason) { this.delayReason = delayReason; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getRequiredDate() { return requiredDate; }
    public void setRequiredDate(String requiredDate) { this.requiredDate = requiredDate; }

    public String getRequiredTime() { return requiredTime; }
    public void setRequiredTime(String requiredTime) { this.requiredTime = requiredTime; }

    public String getExpectedDeliveryTime() { return expectedDeliveryTime; }
    public void setExpectedDeliveryTime(String expectedDeliveryTime) { this.expectedDeliveryTime = expectedDeliveryTime; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public double getLastKnownLatitude() { return lastKnownLatitude; }
    public void setLastKnownLatitude(double lastKnownLatitude) { this.lastKnownLatitude = lastKnownLatitude; }

    public double getLastKnownLongitude() { return lastKnownLongitude; }
    public void setLastKnownLongitude(double lastKnownLongitude) { this.lastKnownLongitude = lastKnownLongitude; }

    public String getLastLocationName() { return lastLocationName; }
    public void setLastLocationName(String lastLocationName) { this.lastLocationName = lastLocationName; }

    public String getLastUpdatedTime() { return lastUpdatedTime; }
    public void setLastUpdatedTime(String lastUpdatedTime) { this.lastUpdatedTime = lastUpdatedTime; }

    public String getLastLocationUpdatedAt() { return lastLocationUpdatedAt != null ? lastLocationUpdatedAt : lastUpdatedTime; }
    public void setLastLocationUpdatedAt(String lastLocationUpdatedAt) { this.lastLocationUpdatedAt = lastLocationUpdatedAt; }

    public String getEstimatedArrival() { return estimatedArrival; }
    public void setEstimatedArrival(String estimatedArrival) { this.estimatedArrival = estimatedArrival; }

    public String getAssignedAt() { return assignedAt; }
    public void setAssignedAt(String assignedAt) { this.assignedAt = assignedAt; }

    public String getPickedUpAt() { return pickedUpAt; }
    public void setPickedUpAt(String pickedUpAt) { this.pickedUpAt = pickedUpAt; }

    public String getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(String deliveredAt) { this.deliveredAt = deliveredAt; }

    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }

    public String getCompletedBy() { return completedBy; }
    public void setCompletedBy(String completedBy) { this.completedBy = completedBy; }

    public String getDeliveryConfirmation() { return deliveryConfirmation; }
    public void setDeliveryConfirmation(String deliveryConfirmation) { this.deliveryConfirmation = deliveryConfirmation; }

    public List<Checkpoint> getCheckpoints() { return checkpoints; }
    public void setCheckpoints(List<Checkpoint> checkpoints) { this.checkpoints = checkpoints; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
