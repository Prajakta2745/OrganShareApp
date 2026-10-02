package com.example.organshare.models.emergency;

import java.io.Serializable;

public class HospitalResourceOverview implements Serializable {
    private String hospitalId;
    private String hospitalName;
    private String location;
    private String contactNumber;
    private int availableBeds;
    private int availableICUBeds;
    private int availableEmergencyBeds;
    private String oPlusBloodStatus; // AVAILABLE, LOW, CRITICAL, OUT_OF_STOCK
    private String transportStatus; // AVAILABLE, NOT_AVAILABLE
    private String hospitalStatus; // OPEN, BUSY, CLOSED
    private String lastUpdatedText;

    public HospitalResourceOverview() {}

    public HospitalResourceOverview(String hospitalId, String hospitalName, String location, String contactNumber,
                                    int availableBeds, int availableICUBeds, int availableEmergencyBeds,
                                    String oPlusBloodStatus, String transportStatus, String hospitalStatus,
                                    String lastUpdatedText) {
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.location = location;
        this.contactNumber = contactNumber;
        this.availableBeds = availableBeds;
        this.availableICUBeds = availableICUBeds;
        this.availableEmergencyBeds = availableEmergencyBeds;
        this.oPlusBloodStatus = oPlusBloodStatus;
        this.transportStatus = transportStatus;
        this.hospitalStatus = hospitalStatus;
        this.lastUpdatedText = lastUpdatedText;
    }

    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public int getAvailableBeds() { return availableBeds; }
    public void setAvailableBeds(int availableBeds) { this.availableBeds = availableBeds; }

    public int getAvailableICUBeds() { return availableICUBeds; }
    public void setAvailableICUBeds(int availableICUBeds) { this.availableICUBeds = availableICUBeds; }

    public int getAvailableEmergencyBeds() { return availableEmergencyBeds; }
    public void setAvailableEmergencyBeds(int availableEmergencyBeds) { this.availableEmergencyBeds = availableEmergencyBeds; }

    public String getoPlusBloodStatus() { return oPlusBloodStatus; }
    public void setoPlusBloodStatus(String oPlusBloodStatus) { this.oPlusBloodStatus = oPlusBloodStatus; }

    public String getTransportStatus() { return transportStatus; }
    public void setTransportStatus(String transportStatus) { this.transportStatus = transportStatus; }

    public String getHospitalStatus() { return hospitalStatus; }
    public void setHospitalStatus(String hospitalStatus) { this.hospitalStatus = hospitalStatus; }

    public String getLastUpdatedText() { return lastUpdatedText; }
    public void setLastUpdatedText(String lastUpdatedText) { this.lastUpdatedText = lastUpdatedText; }
}
