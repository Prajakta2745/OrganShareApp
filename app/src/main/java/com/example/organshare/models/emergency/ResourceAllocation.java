package com.example.organshare.models.emergency;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;

public class ResourceAllocation implements Serializable {
    private String allocationId;
    private String emergencyRequestId;
    private String resourceType; // BEDS, BLOOD, TRANSPORT, VOLUNTEERS, SUPPLIES
    private String resourceId;
    private String resourceName;
    private int quantity;
    private String allocatedBy; // Coordinator ID
    private String coordinatorName;
    private String sourceHospitalId;
    private String destinationHospitalId;
    private String destinationHospitalName;
    private String donorId;
    private String notes;
    private String status; // ALLOCATED, DISPATCHED, COMPLETED, REVERTED

    @ServerTimestamp
    private Date allocatedAt;

    public ResourceAllocation() {}

    public ResourceAllocation(String allocationId, String emergencyRequestId, String resourceType, String resourceId,
                              String resourceName, int quantity, String allocatedBy, String coordinatorName,
                              String destinationHospitalId, String destinationHospitalName) {
        this.allocationId = allocationId;
        this.emergencyRequestId = emergencyRequestId;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.quantity = quantity;
        this.allocatedBy = allocatedBy;
        this.coordinatorName = coordinatorName;
        this.destinationHospitalId = destinationHospitalId;
        this.destinationHospitalName = destinationHospitalName;
        this.status = "ALLOCATED";
    }

    public String getAllocationId() { return allocationId; }
    public void setAllocationId(String allocationId) { this.allocationId = allocationId; }

    public String getEmergencyRequestId() { return emergencyRequestId; }
    public void setEmergencyRequestId(String emergencyRequestId) { this.emergencyRequestId = emergencyRequestId; }

    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }

    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @com.google.firebase.firestore.Exclude
    public int getAllocatedQuantity() { return quantity; }
    @com.google.firebase.firestore.Exclude
    public void setAllocatedQuantity(int quantity) { this.quantity = quantity; }

    public String getAllocatedBy() { return allocatedBy; }
    public void setAllocatedBy(String allocatedBy) { this.allocatedBy = allocatedBy; }

    @com.google.firebase.firestore.Exclude
    public String getCoordinatorId() { return allocatedBy; }
    @com.google.firebase.firestore.Exclude
    public void setCoordinatorId(String coordinatorId) { this.allocatedBy = coordinatorId; }

    public String getCoordinatorName() { return coordinatorName; }
    public void setCoordinatorName(String coordinatorName) { this.coordinatorName = coordinatorName; }

    public String getSourceHospitalId() { return sourceHospitalId; }
    public void setSourceHospitalId(String sourceHospitalId) { this.sourceHospitalId = sourceHospitalId; }

    @com.google.firebase.firestore.Exclude
    public String getSourceFacilityName() { return sourceHospitalId; }
    @com.google.firebase.firestore.Exclude
    public void setSourceFacilityName(String sourceFacilityName) { this.sourceHospitalId = sourceFacilityName; }

    public String getDestinationHospitalId() { return destinationHospitalId; }
    public void setDestinationHospitalId(String destinationHospitalId) { this.destinationHospitalId = destinationHospitalId; }

    public String getDestinationHospitalName() { return destinationHospitalName; }
    public void setDestinationHospitalName(String destinationHospitalName) { this.destinationHospitalName = destinationHospitalName; }

    @com.google.firebase.firestore.Exclude
    public String getTargetFacilityName() { return destinationHospitalName; }
    @com.google.firebase.firestore.Exclude
    public void setTargetFacilityName(String targetFacilityName) { this.destinationHospitalName = targetFacilityName; }

    public String getDonorId() { return donorId; }
    public void setDonorId(String donorId) { this.donorId = donorId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getAllocatedAt() { return allocatedAt; }
    public void setAllocatedAt(Date allocatedAt) { this.allocatedAt = allocatedAt; }
}
