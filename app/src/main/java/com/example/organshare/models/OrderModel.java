package com.example.organshare.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;

public class OrderModel implements Serializable {
    private String orderId; // e.g. ORD-2026-00001
    private String requestId;
    private String allocationId;
    private String hospitalId;
    private String hospitalName;
    private String hospitalAddress;
    private double hospitalLatitude;
    private double hospitalLongitude;
    private String hospitalContact;
    private String organBankId;
    private String organBankName;
    private String pickupAddress;
    private double pickupLatitude;
    private double pickupLongitude;
    private String organInventoryId;
    private String organType;
    private String bloodGroup;
    private String orderDate;
    private String orderTime;
    private String orderStatus; // PENDING_ASSIGNMENT, ASSIGNED, IN_TRANSIT, DELIVERED, COMPLETED, CANCELLED
    private String deliveryPersonId;
    private String deliveryPersonName;
    private String deliveryPersonPhone;
    private String expectedDeliveryTime;
    private String deliveryId;

    @ServerTimestamp
    private Date createdAt;
    @ServerTimestamp
    private Date updatedAt;

    public OrderModel() {}

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getAllocationId() { return allocationId; }
    public void setAllocationId(String allocationId) { this.allocationId = allocationId; }

    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public String getHospitalAddress() { return hospitalAddress; }
    public void setHospitalAddress(String hospitalAddress) { this.hospitalAddress = hospitalAddress; }

    public double getHospitalLatitude() { return hospitalLatitude; }
    public void setHospitalLatitude(double hospitalLatitude) { this.hospitalLatitude = hospitalLatitude; }

    public double getHospitalLongitude() { return hospitalLongitude; }
    public void setHospitalLongitude(double hospitalLongitude) { this.hospitalLongitude = hospitalLongitude; }

    public String getHospitalContact() { return hospitalContact; }
    public void setHospitalContact(String hospitalContact) { this.hospitalContact = hospitalContact; }

    public String getOrganBankId() { return organBankId; }
    public void setOrganBankId(String organBankId) { this.organBankId = organBankId; }

    public String getOrganBankName() { return organBankName; }
    public void setOrganBankName(String organBankName) { this.organBankName = organBankName; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    public double getPickupLatitude() { return pickupLatitude; }
    public void setPickupLatitude(double pickupLatitude) { this.pickupLatitude = pickupLatitude; }

    public double getPickupLongitude() { return pickupLongitude; }
    public void setPickupLongitude(double pickupLongitude) { this.pickupLongitude = pickupLongitude; }

    public String getOrganInventoryId() { return organInventoryId; }
    public void setOrganInventoryId(String organInventoryId) { this.organInventoryId = organInventoryId; }

    public String getOrganType() { return organType; }
    public void setOrganType(String organType) { this.organType = organType; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getOrderTime() { return orderTime; }
    public void setOrderTime(String orderTime) { this.orderTime = orderTime; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public String getDeliveryPersonId() { return deliveryPersonId; }
    public void setDeliveryPersonId(String deliveryPersonId) { this.deliveryPersonId = deliveryPersonId; }

    public String getDeliveryPersonName() { return deliveryPersonName; }
    public void setDeliveryPersonName(String deliveryPersonName) { this.deliveryPersonName = deliveryPersonName; }

    public String getDeliveryPersonPhone() { return deliveryPersonPhone; }
    public void setDeliveryPersonPhone(String deliveryPersonPhone) { this.deliveryPersonPhone = deliveryPersonPhone; }

    public String getExpectedDeliveryTime() { return expectedDeliveryTime; }
    public void setExpectedDeliveryTime(String expectedDeliveryTime) { this.expectedDeliveryTime = expectedDeliveryTime; }

    public String getDeliveryId() { return deliveryId; }
    public void setDeliveryId(String deliveryId) { this.deliveryId = deliveryId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
