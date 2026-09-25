package com.example.organshare.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;

public class BloodRequest implements Serializable {
    private String requestId; // REQ-BLD-2026-XXXXX
    private String hospitalId;
    private String hospitalName;
    private String hospitalAddress;
    private String hospitalContact;
    private String bloodGroup; // A+, A-, B+, B-, AB+, AB-, O+, O-
    private int unitsRequested;
    private int unitsFulfilled;
    private int unitsRemaining;
    private String patientRefId;
    private String requiredDate;
    private String requiredTime;
    private String emergencyLevel; // NORMAL, URGENT, EMERGENCY/CRITICAL
    private String status; // PENDING, UNDER_REVIEW, CLINICALLY_AUTHORIZED, APPROVED, ACTIVE, PARTIALLY_FULFILLED, FULFILLED, REJECTED, CANCELLED
    private String additionalNotes;

    @ServerTimestamp
    private Date createdAt;
    @ServerTimestamp
    private Date updatedAt;

    public BloodRequest() {}

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

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public int getUnitsRequested() { return unitsRequested; }
    public void setUnitsRequested(int unitsRequested) { this.unitsRequested = unitsRequested; }

    public int getUnitsFulfilled() { return unitsFulfilled; }
    public void setUnitsFulfilled(int unitsFulfilled) { this.unitsFulfilled = unitsFulfilled; }

    public int getUnitsRemaining() { return unitsRemaining; }
    public void setUnitsRemaining(int unitsRemaining) { this.unitsRemaining = unitsRemaining; }

    public String getPatientRefId() { return patientRefId; }
    public void setPatientRefId(String patientRefId) { this.patientRefId = patientRefId; }

    public String getRequiredDate() { return requiredDate; }
    public void setRequiredDate(String requiredDate) { this.requiredDate = requiredDate; }

    public String getRequiredTime() { return requiredTime; }
    public void setRequiredTime(String requiredTime) { this.requiredTime = requiredTime; }

    public String getEmergencyLevel() { return emergencyLevel; }
    public void setEmergencyLevel(String emergencyLevel) { this.emergencyLevel = emergencyLevel; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAdditionalNotes() { return additionalNotes; }
    public void setAdditionalNotes(String additionalNotes) { this.additionalNotes = additionalNotes; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
