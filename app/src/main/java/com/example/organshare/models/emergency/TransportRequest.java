package com.example.organshare.models.emergency;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;

public class TransportRequest implements Serializable {
    private String transportRequestId;
    private String donorId;
    private String donorName;
    private String donorPhone;
    private String pickupLocation;
    private String destinationHospitalId;
    private String destinationHospitalName;
    private String emergencyLevel; // CRITICAL, URGENT, NORMAL
    private String requestedVehicleType;
    private String notes;
    private String assignedTransportId;
    private String assignedVehicleNumber;
    private String assignedDriverName;
    private String assignedDriverPhone;
    private String status; // PENDING, SEARCHING, ASSIGNED, IN_TRANSIT, COMPLETED, CANCELLED

    @ServerTimestamp
    private Date requestedAt;
    @ServerTimestamp
    private Date lastUpdated;

    public TransportRequest() {}

    public TransportRequest(String transportRequestId, String donorId, String donorName, String donorPhone,
                            String pickupLocation, String destinationHospitalId, String destinationHospitalName,
                            String emergencyLevel) {
        this.transportRequestId = transportRequestId;
        this.donorId = donorId;
        this.donorName = donorName;
        this.donorPhone = donorPhone;
        this.pickupLocation = pickupLocation;
        this.destinationHospitalId = destinationHospitalId;
        this.destinationHospitalName = destinationHospitalName;
        this.emergencyLevel = emergencyLevel;
        this.status = "SEARCHING";
    }

    public String getTransportRequestId() { return transportRequestId; }
    public void setTransportRequestId(String transportRequestId) { this.transportRequestId = transportRequestId; }

    public String getDonorId() { return donorId; }
    public void setDonorId(String donorId) { this.donorId = donorId; }

    public String getDonorName() { return donorName; }
    public void setDonorName(String donorName) { this.donorName = donorName; }

    public String getDonorPhone() { return donorPhone; }
    public void setDonorPhone(String donorPhone) { this.donorPhone = donorPhone; }

    @com.google.firebase.firestore.Exclude
    public String getRequesterId() { return donorId; }
    @com.google.firebase.firestore.Exclude
    public void setRequesterId(String requesterId) { this.donorId = requesterId; }

    @com.google.firebase.firestore.Exclude
    public String getRequesterName() { return donorName; }
    @com.google.firebase.firestore.Exclude
    public void setRequesterName(String requesterName) { this.donorName = requesterName; }

    @com.google.firebase.firestore.Exclude
    public String getRequesterPhone() { return donorPhone; }
    @com.google.firebase.firestore.Exclude
    public void setRequesterPhone(String requesterPhone) { this.donorPhone = requesterPhone; }

    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }

    public String getDestinationHospitalId() { return destinationHospitalId; }
    public void setDestinationHospitalId(String destinationHospitalId) { this.destinationHospitalId = destinationHospitalId; }

    public String getDestinationHospitalName() { return destinationHospitalName; }
    public void setDestinationHospitalName(String destinationHospitalName) { this.destinationHospitalName = destinationHospitalName; }

    public String getEmergencyLevel() { return emergencyLevel; }
    public void setEmergencyLevel(String emergencyLevel) { this.emergencyLevel = emergencyLevel; }

    @com.google.firebase.firestore.Exclude
    public boolean isEmergency() {
        return "CRITICAL".equalsIgnoreCase(emergencyLevel) || "EMERGENCY".equalsIgnoreCase(emergencyLevel) || "URGENT".equalsIgnoreCase(emergencyLevel);
    }
    @com.google.firebase.firestore.Exclude
    public void setEmergency(boolean emergency) {
        this.emergencyLevel = emergency ? "CRITICAL" : "NORMAL";
    }

    public String getRequestedVehicleType() { return requestedVehicleType; }
    public void setRequestedVehicleType(String requestedVehicleType) { this.requestedVehicleType = requestedVehicleType; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getAssignedTransportId() { return assignedTransportId; }
    public void setAssignedTransportId(String assignedTransportId) { this.assignedTransportId = assignedTransportId; }

    public String getAssignedVehicleNumber() { return assignedVehicleNumber; }
    public void setAssignedVehicleNumber(String assignedVehicleNumber) { this.assignedVehicleNumber = assignedVehicleNumber; }

    public String getAssignedDriverName() { return assignedDriverName; }
    public void setAssignedDriverName(String assignedDriverName) { this.assignedDriverName = assignedDriverName; }

    public String getAssignedDriverPhone() { return assignedDriverPhone; }
    public void setAssignedDriverPhone(String assignedDriverPhone) { this.assignedDriverPhone = assignedDriverPhone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getRequestedAt() { return requestedAt; }
    public void setRequestedAt(Date requestedAt) { this.requestedAt = requestedAt; }

    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }
}
