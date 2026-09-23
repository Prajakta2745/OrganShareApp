package com.example.organshare.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;

public class HospitalProfile implements Serializable {
    private String hospitalId;
    private String userId;
    private String hospitalName;
    private String licenseNumber;
    private String address;
    private String city;
    private String state;
    private double latitude;
    private double longitude;
    private String contactNumber;
    private String transplantCoordinatorPhone;
    private String email;
    private boolean verified; // Admin approval required

    @ServerTimestamp
    private Date createdAt;
    @ServerTimestamp
    private Date updatedAt;

    public HospitalProfile() {}

    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

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

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getTransplantCoordinatorPhone() { return transplantCoordinatorPhone; }
    public void setTransplantCoordinatorPhone(String transplantCoordinatorPhone) { this.transplantCoordinatorPhone = transplantCoordinatorPhone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
