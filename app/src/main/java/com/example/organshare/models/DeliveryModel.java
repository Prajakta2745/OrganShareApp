package com.example.organshare.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeliveryModel implements Serializable {
    private String deliveryId;
    private String orderId;
    private String requestId;
    private String organType;
    private String bloodGroup;
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
    private String currentStatus; // ASSIGNED, PICKUP_STARTED, PICKED_UP, IN_TRANSIT, CHECKPOINT_1, CHECKPOINT_2, NEAR_DESTINATION, DELIVERED, COMPLETED, DELAYED
    private String delayReason;
    private double lastKnownLatitude;
    private double lastKnownLongitude;
    private String lastUpdatedTime;
    private String estimatedArrival;
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

    public double getLastKnownLatitude() { return lastKnownLatitude; }
    public void setLastKnownLatitude(double lastKnownLatitude) { this.lastKnownLatitude = lastKnownLatitude; }

    public double getLastKnownLongitude() { return lastKnownLongitude; }
    public void setLastKnownLongitude(double lastKnownLongitude) { this.lastKnownLongitude = lastKnownLongitude; }

    public String getLastUpdatedTime() { return lastUpdatedTime; }
    public void setLastUpdatedTime(String lastUpdatedTime) { this.lastUpdatedTime = lastUpdatedTime; }

    public String getEstimatedArrival() { return estimatedArrival; }
    public void setEstimatedArrival(String estimatedArrival) { this.estimatedArrival = estimatedArrival; }

    public List<Checkpoint> getCheckpoints() { return checkpoints; }
    public void setCheckpoints(List<Checkpoint> checkpoints) { this.checkpoints = checkpoints; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
