package com.example.organshare.models.emergency;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;

public class MedicalSupplyResource implements Serializable {
    private String supplyId;
    private String hospitalId;
    private String hospitalName;
    private String supplyName; // Oxygen Cylinders, PPE Kits, Surgical Packs, IV Fluids, etc.
    private String category; // OXYGEN, MEDICINES, PPE, SURGICAL, IV_FLUIDS, EQUIPMENT
    private int availableQuantity;
    private int minimumRequiredQuantity;
    private String unit; // Cylinders, Boxes, Kits, Liters, Vials
    private String storageLocation;
    private String expiryDate;
    private String status; // NORMAL, LOW, CRITICAL, OUT_OF_STOCK

    @ServerTimestamp
    private Date lastUpdated;

    public MedicalSupplyResource() {}

    public MedicalSupplyResource(String supplyId, String hospitalId, String hospitalName, String supplyName,
                                 String category, int availableQuantity, int minimumRequiredQuantity,
                                 String unit, String storageLocation, String expiryDate) {
        this.supplyId = supplyId;
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.supplyName = supplyName;
        this.category = category;
        this.availableQuantity = availableQuantity;
        this.minimumRequiredQuantity = minimumRequiredQuantity;
        this.unit = unit;
        this.storageLocation = storageLocation;
        this.expiryDate = expiryDate;
        updateStatus();
    }

    public void updateStatus() {
        if (availableQuantity <= 0) {
            this.status = "OUT_OF_STOCK";
        } else if (availableQuantity <= Math.max(1, minimumRequiredQuantity / 4)) {
            this.status = "CRITICAL";
        } else if (availableQuantity < minimumRequiredQuantity) {
            this.status = "LOW";
        } else {
            this.status = "NORMAL";
        }
    }

    public String getSupplyId() { return supplyId; }
    public void setSupplyId(String supplyId) { this.supplyId = supplyId; }

    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public String getSupplyName() { return supplyName; }
    public void setSupplyName(String supplyName) { this.supplyName = supplyName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
        updateStatus();
    }

    public int getMinimumRequiredQuantity() { return minimumRequiredQuantity; }
    public void setMinimumRequiredQuantity(int minimumRequiredQuantity) {
        this.minimumRequiredQuantity = minimumRequiredQuantity;
        updateStatus();
    }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @com.google.firebase.firestore.Exclude
    public String getItemName() { return getSupplyName(); }
    @com.google.firebase.firestore.Exclude
    public void setItemName(String name) { setSupplyName(name); }

    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }
}
