package com.example.organshare.models.emergency;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;

public class HospitalBedResource implements Serializable {
    private String hospitalId;
    private String hospitalName;
    private String location;
    private int totalBeds;
    private int availableBeds;
    private int occupiedBeds;
    private int totalICUBeds;
    private int availableICUBeds;
    private int totalEmergencyBeds;
    private int availableEmergencyBeds;
    private String status; // AVAILABLE, LOW_CAPACITY, FULL, CLOSED
    private String contactNumber;

    @ServerTimestamp
    private Date lastUpdated;

    public HospitalBedResource() {}

    public HospitalBedResource(String hospitalId, String hospitalName, String location, int totalBeds, int availableBeds,
                               int totalICUBeds, int availableICUBeds, int totalEmergencyBeds, int availableEmergencyBeds,
                               String status, String contactNumber) {
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.location = location;
        this.totalBeds = totalBeds;
        this.availableBeds = availableBeds;
        this.occupiedBeds = Math.max(0, totalBeds - availableBeds);
        this.totalICUBeds = totalICUBeds;
        this.availableICUBeds = availableICUBeds;
        this.totalEmergencyBeds = totalEmergencyBeds;
        this.availableEmergencyBeds = availableEmergencyBeds;
        this.status = status;
        this.contactNumber = contactNumber;
    }

    public HospitalBedResource(String hospitalId, String hospitalName, int totalGeneralBeds, int availableGeneralBeds,
                               int totalIcuBeds, int availableIcuBeds, int totalEmergencyBeds, int availableEmergencyBeds,
                               boolean isAcceptingDonors) {
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.totalBeds = totalGeneralBeds;
        this.availableBeds = availableGeneralBeds;
        this.occupiedBeds = Math.max(0, totalGeneralBeds - availableGeneralBeds);
        this.totalICUBeds = totalIcuBeds;
        this.availableICUBeds = availableIcuBeds;
        this.totalEmergencyBeds = totalEmergencyBeds;
        this.availableEmergencyBeds = availableEmergencyBeds;
        this.status = isAcceptingDonors ? "AVAILABLE" : "CLOSED";
    }

    @Exclude
    public int getAvailableGeneralBeds() { return getAvailableBeds(); }
    @Exclude
    public void setAvailableGeneralBeds(int beds) { setAvailableBeds(beds); }

    @Exclude
    public int getTotalGeneralBeds() { return getTotalBeds(); }
    @Exclude
    public void setTotalGeneralBeds(int beds) { setTotalBeds(beds); }

    @Exclude
    public int getAvailableIcuBeds() { return getAvailableICUBeds(); }
    @Exclude
    public void setAvailableIcuBeds(int beds) { setAvailableICUBeds(beds); }

    @Exclude
    public int getTotalIcuBeds() { return getTotalICUBeds(); }
    @Exclude
    public void setTotalIcuBeds(int beds) { setTotalICUBeds(beds); }

    @Exclude
    public boolean isAcceptingDonors() {
        return !"CLOSED".equalsIgnoreCase(this.status) && !"FULL".equalsIgnoreCase(this.status);
    }
    @Exclude
    public void setAcceptingDonors(boolean accepting) {
        this.status = accepting ? "AVAILABLE" : "CLOSED";
    }

    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getTotalBeds() { return totalBeds; }
    public void setTotalBeds(int totalBeds) { this.totalBeds = totalBeds; }

    public int getAvailableBeds() { return availableBeds; }
    public void setAvailableBeds(int availableBeds) {
        this.availableBeds = availableBeds;
        this.occupiedBeds = Math.max(0, this.totalBeds - availableBeds);
    }

    public int getOccupiedBeds() { return occupiedBeds; }
    public void setOccupiedBeds(int occupiedBeds) { this.occupiedBeds = occupiedBeds; }

    public int getTotalICUBeds() { return totalICUBeds; }
    public void setTotalICUBeds(int totalICUBeds) { this.totalICUBeds = totalICUBeds; }

    public int getAvailableICUBeds() { return availableICUBeds; }
    public void setAvailableICUBeds(int availableICUBeds) { this.availableICUBeds = availableICUBeds; }

    public int getTotalEmergencyBeds() { return totalEmergencyBeds; }
    public void setTotalEmergencyBeds(int totalEmergencyBeds) { this.totalEmergencyBeds = totalEmergencyBeds; }

    public int getAvailableEmergencyBeds() { return availableEmergencyBeds; }
    public void setAvailableEmergencyBeds(int availableEmergencyBeds) { this.availableEmergencyBeds = availableEmergencyBeds; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }
}
