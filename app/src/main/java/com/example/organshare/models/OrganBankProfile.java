package com.example.organshare.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;

public class OrganBankProfile implements Serializable {
    private String bankId;
    private String userId;
    private String bankName;
    private String licenseNumber;
    private String address;
    private String city;
    private String state;
    private double latitude;
    private double longitude;
    private String contactPhone;
    private String emergencyHotline;
    private String email;
    private boolean verified;

    @ServerTimestamp
    private Date createdAt;
    @ServerTimestamp
    private Date updatedAt;

    public OrganBankProfile() {}

    public String getBankId() { return bankId; }
    public void setBankId(String bankId) { this.bankId = bankId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getEmergencyHotline() { return emergencyHotline; }
    public void setEmergencyHotline(String emergencyHotline) { this.emergencyHotline = emergencyHotline; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
