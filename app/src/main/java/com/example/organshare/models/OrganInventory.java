package com.example.organshare.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;

public class OrganInventory implements Serializable {
    private String inventoryId;
    private String organType; // Kidney, Liver, Heart, Lungs, Pancreas, Cornea, Bone Marrow
    private String bloodGroup; // A+, A-, B+, B-, AB+, AB-, O+, O-
    private String availabilityStatus; // AVAILABLE, RESERVED, ALLOCATED, IN_TRANSIT, DELIVERED, UNAVAILABLE
    private String collectionDate;
    private String expiryDate;
    private int preservationLimitHours;
    private String donorRefId;
    private String donorMaskedName;
    private String organBankId;
    private String organBankName;
    private String storageLocation;
    private String storageTemperature; // e.g. "4°C Hypothermic Preservation"
    private String notes;
    private boolean verified;

    @ServerTimestamp
    private Date createdAt;
    @ServerTimestamp
    private Date updatedAt;

    public OrganInventory() {}

    public String getInventoryId() { return inventoryId; }
    public void setInventoryId(String inventoryId) { this.inventoryId = inventoryId; }

    public String getOrganType() { return organType; }
    public void setOrganType(String organType) { this.organType = organType; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }

    public String getCollectionDate() { return collectionDate; }
    public void setCollectionDate(String collectionDate) { this.collectionDate = collectionDate; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public int getPreservationLimitHours() { return preservationLimitHours; }
    public void setPreservationLimitHours(int preservationLimitHours) { this.preservationLimitHours = preservationLimitHours; }

    public String getDonorRefId() { return donorRefId; }
    public void setDonorRefId(String donorRefId) { this.donorRefId = donorRefId; }

    public String getDonorMaskedName() { return donorMaskedName; }
    public void setDonorMaskedName(String donorMaskedName) { this.donorMaskedName = donorMaskedName; }

    public String getOrganBankId() { return organBankId; }
    public void setOrganBankId(String organBankId) { this.organBankId = organBankId; }

    public String getOrganBankName() { return organBankName; }
    public void setOrganBankName(String organBankName) { this.organBankName = organBankName; }

    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }

    public String getStorageTemperature() { return storageTemperature; }
    public void setStorageTemperature(String storageTemperature) { this.storageTemperature = storageTemperature; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
