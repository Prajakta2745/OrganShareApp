package com.example.organshare.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;

public class BloodInventory implements Serializable {
    private String unitId; // BLD-5001
    private String bloodGroup; // A+, A-, B+, B-, AB+, AB-, O+, O-
    private int unitsCount; // e.g. 1 unit (450ml)
    private String collectionDate;
    private String expiryDate;
    private String organBankId;
    private String organBankName;
    private String storageLocation;
    private String status; // AVAILABLE, RESERVED, ALLOCATED, DISPATCHED, DELIVERED, EXPIRED, UNAVAILABLE
    private boolean verified;
    private String notes;

    @ServerTimestamp
    private Date createdAt;
    @ServerTimestamp
    private Date updatedAt;

    public BloodInventory() {}

    public String getUnitId() { return unitId; }
    public void setUnitId(String unitId) { this.unitId = unitId; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public int getUnitsCount() { return unitsCount; }
    public void setUnitsCount(int unitsCount) { this.unitsCount = unitsCount; }

    public String getCollectionDate() { return collectionDate; }
    public void setCollectionDate(String collectionDate) { this.collectionDate = collectionDate; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getOrganBankId() { return organBankId; }
    public void setOrganBankId(String organBankId) { this.organBankId = organBankId; }

    public String getOrganBankName() { return organBankName; }
    public void setOrganBankName(String organBankName) { this.organBankName = organBankName; }

    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
