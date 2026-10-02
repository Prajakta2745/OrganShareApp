package com.example.organshare.models.emergency;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;

public class BloodResource implements Serializable {
    private String bloodResourceId;
    private String hospitalId;
    private String hospitalName;
    private String bloodGroup; // A+, A-, B+, B-, AB+, AB-, O+, O-
    private int availableUnits;
    private int minimumRequiredUnits;
    private String status; // NORMAL, LOW, CRITICAL, OUT_OF_STOCK

    @ServerTimestamp
    private Date lastUpdated;

    public BloodResource() {}

    public BloodResource(String bloodResourceId, String hospitalId, String hospitalName, String bloodGroup,
                         int availableUnits, int minimumRequiredUnits) {
        this.bloodResourceId = bloodResourceId;
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.bloodGroup = bloodGroup;
        this.availableUnits = availableUnits;
        this.minimumRequiredUnits = minimumRequiredUnits;
        updateStatus();
    }

    public void updateStatus() {
        if (availableUnits <= 0) {
            this.status = "OUT_OF_STOCK";
        } else if (availableUnits <= 3) {
            this.status = "CRITICAL";
        } else if (availableUnits < minimumRequiredUnits) {
            this.status = "LOW";
        } else {
            this.status = "NORMAL";
        }
    }

    public String getBloodResourceId() { return bloodResourceId; }
    public void setBloodResourceId(String bloodResourceId) { this.bloodResourceId = bloodResourceId; }

    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public int getAvailableUnits() { return availableUnits; }
    public void setAvailableUnits(int availableUnits) {
        this.availableUnits = availableUnits;
        updateStatus();
    }

    public int getMinimumRequiredUnits() { return minimumRequiredUnits; }
    public void setMinimumRequiredUnits(int minimumRequiredUnits) {
        this.minimumRequiredUnits = minimumRequiredUnits;
        updateStatus();
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @com.google.firebase.firestore.Exclude
    public String getRhFactor() {
        if (bloodGroup != null && bloodGroup.endsWith("+")) return "Positive";
        if (bloodGroup != null && bloodGroup.endsWith("-")) return "Negative";
        return "Positive";
    }
    @com.google.firebase.firestore.Exclude
    public void setRhFactor(String rh) {
        // preserved for compatibility
    }

    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }
}
