package com.example.organshare.models.emergency;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;

public class TransportResource implements Serializable {
    private String transportId;
    private String vehicleId; // e.g. AMB-04
    private String vehicleType; // Advanced Life Support, Basic Life Support, Patient Transport
    private String driverName;
    private String driverContact;
    private String currentLocation;
    private String destination;
    private String availabilityStatus; // AVAILABLE, ASSIGNED, IN_TRANSIT, MAINTENANCE, UNAVAILABLE
    private String assignedHospitalId;
    private String assignedDonorId;
    private String assignedEmergencyRequestId;
    private String currentAssignment;

    @ServerTimestamp
    private Date lastUpdated;

    public TransportResource() {}

    public TransportResource(String transportId, String vehicleId, String vehicleType, String driverName,
                             String driverContact, String currentLocation, String availabilityStatus,
                             String assignedHospitalId) {
        this.transportId = transportId;
        this.vehicleId = vehicleId;
        this.vehicleType = vehicleType;
        this.driverName = driverName;
        this.driverContact = driverContact;
        this.currentLocation = currentLocation;
        this.availabilityStatus = availabilityStatus;
        this.assignedHospitalId = assignedHospitalId;
    }

    public String getTransportId() { return transportId; }
    public void setTransportId(String transportId) { this.transportId = transportId; }

    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverContact() { return driverContact; }
    public void setDriverContact(String driverContact) { this.driverContact = driverContact; }

    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }

    public String getAssignedHospitalId() { return assignedHospitalId; }
    public void setAssignedHospitalId(String assignedHospitalId) { this.assignedHospitalId = assignedHospitalId; }

    public String getAssignedDonorId() { return assignedDonorId; }
    public void setAssignedDonorId(String assignedDonorId) { this.assignedDonorId = assignedDonorId; }

    public String getAssignedEmergencyRequestId() { return assignedEmergencyRequestId; }
    public void setAssignedEmergencyRequestId(String assignedEmergencyRequestId) { this.assignedEmergencyRequestId = assignedEmergencyRequestId; }

    public String getCurrentAssignment() { return currentAssignment; }
    public void setCurrentAssignment(String currentAssignment) { this.currentAssignment = currentAssignment; }

    @com.google.firebase.firestore.Exclude
    public String getVehicleNumber() { return getVehicleId(); }
    @com.google.firebase.firestore.Exclude
    public void setVehicleNumber(String num) { setVehicleId(num); }

    @com.google.firebase.firestore.Exclude
    public String getDriverPhone() { return getDriverContact(); }
    @com.google.firebase.firestore.Exclude
    public void setDriverPhone(String phone) { setDriverContact(phone); }

    @com.google.firebase.firestore.Exclude
    public String getCurrentLocationName() { return getCurrentLocation(); }
    @com.google.firebase.firestore.Exclude
    public void setCurrentLocationName(String loc) { setCurrentLocation(loc); }

    @com.google.firebase.firestore.Exclude
    public String getStatus() { return getAvailabilityStatus(); }
    @com.google.firebase.firestore.Exclude
    public void setStatus(String status) { setAvailabilityStatus(status); }

    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }
}
