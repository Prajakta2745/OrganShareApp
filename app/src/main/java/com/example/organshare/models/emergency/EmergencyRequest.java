package com.example.organshare.models.emergency;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;

public class EmergencyRequest implements Serializable {
    private String emergencyRequestId;
    private String requestedBy; // Hospital / User / Coordinator ID
    private String requesterName;
    private String donorId;
    private String hospitalId;
    private String hospitalName;
    private String resourceType; // BEDS, BLOOD, TRANSPORT, VOLUNTEERS, SUPPLIES
    private String resourceDetails; // e.g. "O+ Blood", "ICU Beds", "Oxygen Cylinders"
    private int requiredQuantity;
    private int allocatedQuantity;
    private String priority; // CRITICAL, HIGH, MEDIUM, LOW
    private String description;
    private String assignedCoordinatorId;
    private String assignedCoordinatorName;
    private String status; // PENDING, IN_PROGRESS, FULFILLED, PARTIALLY_FULFILLED, CANCELLED

    @ServerTimestamp
    private Date requestedAt;
    @ServerTimestamp
    private Date lastUpdated;

    public EmergencyRequest() {}

    public EmergencyRequest(String emergencyRequestId, String requestedBy, String requesterName,
                            String hospitalId, String hospitalName, String resourceType, String resourceDetails,
                            int requiredQuantity, String priority, String description) {
        this.emergencyRequestId = emergencyRequestId;
        this.requestedBy = requestedBy;
        this.requesterName = requesterName;
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.resourceType = resourceType;
        this.resourceDetails = resourceDetails;
        this.requiredQuantity = requiredQuantity;
        this.allocatedQuantity = 0;
        this.priority = priority;
        this.description = description;
        this.status = "PENDING";
    }

    public String getEmergencyRequestId() { return emergencyRequestId; }
    public void setEmergencyRequestId(String emergencyRequestId) { this.emergencyRequestId = emergencyRequestId; }

    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }

    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }

    public String getDonorId() { return donorId; }
    public void setDonorId(String donorId) { this.donorId = donorId; }

    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public String getResourceDetails() { return resourceDetails; }
    public void setResourceDetails(String resourceDetails) { this.resourceDetails = resourceDetails; }

    public int getRequiredQuantity() { return requiredQuantity; }
    public void setRequiredQuantity(int requiredQuantity) { this.requiredQuantity = requiredQuantity; }

    public int getAllocatedQuantity() { return allocatedQuantity; }
    public void setAllocatedQuantity(int allocatedQuantity) { this.allocatedQuantity = allocatedQuantity; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAssignedCoordinatorId() { return assignedCoordinatorId; }
    public void setAssignedCoordinatorId(String assignedCoordinatorId) { this.assignedCoordinatorId = assignedCoordinatorId; }

    public String getAssignedCoordinatorName() { return assignedCoordinatorName; }
    public void setAssignedCoordinatorName(String assignedCoordinatorName) { this.assignedCoordinatorName = assignedCoordinatorName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getRequestedAt() { return requestedAt; }
    public void setRequestedAt(Date requestedAt) { this.requestedAt = requestedAt; }

    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }
}
