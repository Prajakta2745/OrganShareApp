package com.example.organshare.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;

public class DeliveryPersonnelProfile implements Serializable {
    private String deliveryPersonId;
    private String userId;
    private String fullName;
    private String badgeNumber;
    private String phone;
    private String email;
    private String vehicleType; // Medical Van, Rapid Courier, Air Ambulance
    private String vehiclePlateNumber;
    private String currentStatus; // AVAILABLE, IN_TRANSIT, OFFLINE
    private double currentLatitude;
    private double currentLongitude;
    private boolean verified;

    @ServerTimestamp
    private Date createdAt;
    @ServerTimestamp
    private Date updatedAt;

    public DeliveryPersonnelProfile() {}

    public String getDeliveryPersonId() { return deliveryPersonId; }
    public void setDeliveryPersonId(String deliveryPersonId) { this.deliveryPersonId = deliveryPersonId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getBadgeNumber() { return badgeNumber; }
    public void setBadgeNumber(String badgeNumber) { this.badgeNumber = badgeNumber; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getVehiclePlateNumber() { return vehiclePlateNumber; }
    public void setVehiclePlateNumber(String vehiclePlateNumber) { this.vehiclePlateNumber = vehiclePlateNumber; }

    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }

    public double getCurrentLatitude() { return currentLatitude; }
    public void setCurrentLatitude(double currentLatitude) { this.currentLatitude = currentLatitude; }

    public double getCurrentLongitude() { return currentLongitude; }
    public void setCurrentLongitude(double currentLongitude) { this.currentLongitude = currentLongitude; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
