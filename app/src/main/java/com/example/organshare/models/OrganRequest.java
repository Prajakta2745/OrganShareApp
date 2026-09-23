package com.example.organshare.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;

public class OrganRequest implements Serializable {
    private String requestId; // e.g. REQ-2026-00001
    private String hospitalId;
    private String hospitalName;
    private String hospitalAddress;
    private String hospitalContact;
    private String organRequired;
    private String bloodGroup;
    private int quantity;
    private String emergencyLevel; // EMERGENCY, URGENT, NORMAL
    private String patientRefId;
    private String requiredDate;
    private String requiredTime;
    private String additionalNotes;
    private String status; // PENDING, UNDER_REVIEW, APPROVED, REJECTED, ALLOCATED, IN_TRANSIT, DELIVERED, COMPLETED, CANCELLED
    private String matchedInventoryId;
    private String allocatedOrderId;
    private String compatibilitySummary; // Compatible, Potentially Compatible, Not Available

    @ServerTimestamp
    private Date createdAt;
    @ServerTimestamp
    private Date updatedAt;

    public OrganRequest() {}

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public String getHospitalAddress() { return hospitalAddress; }
    public void setHospitalAddress(String hospitalAddress) { this.hospitalAddress = hospitalAddress; }

    public String getHospitalContact() { return hospitalContact; }
    public void setHospitalContact(String hospitalContact) { this.hospitalContact = hospitalContact; }

    public String getOrganRequired() { return organRequired; }
    public void setOrganRequired(String organRequired) { this.organRequired = organRequired; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getEmergencyLevel() { return emergencyLevel; }
    public void setEmergencyLevel(String emergencyLevel) { this.emergencyLevel = emergencyLevel; }

    public String getPatientRefId() { return patientRefId; }
    public void setPatientRefId(String patientRefId) { this.patientRefId = patientRefId; }

    public String getRequiredDate() { return requiredDate; }
    public void setRequiredDate(String requiredDate) { this.requiredDate = requiredDate; }

    public String getRequiredTime() { return requiredTime; }
    public void setRequiredTime(String requiredTime) { this.requiredTime = requiredTime; }

    public String getAdditionalNotes() { return additionalNotes; }
    public void setAdditionalNotes(String additionalNotes) { this.additionalNotes = additionalNotes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMatchedInventoryId() { return matchedInventoryId; }
    public void setMatchedInventoryId(String matchedInventoryId) { this.matchedInventoryId = matchedInventoryId; }

    public String getAllocatedOrderId() { return allocatedOrderId; }
    public void setAllocatedOrderId(String allocatedOrderId) { this.allocatedOrderId = allocatedOrderId; }

    public String getCompatibilitySummary() { return compatibilitySummary; }
    public void setCompatibilitySummary(String compatibilitySummary) { this.compatibilitySummary = compatibilitySummary; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
