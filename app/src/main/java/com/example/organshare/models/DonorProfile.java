package com.example.organshare.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DonorProfile implements Serializable {
    private String donorId;
    private String userId;
    private String firstName;
    private String lastName;
    private String gender;
    private String dob;
    private String bloodGroup;
    private String phone;
    private String email;
    private String city;
    private String state;
    private String generalLocation;
    private String emergencyContact;
    private List<String> organsWillingToDonate = new ArrayList<>();
    private String donationStatus; // PLEDGED, ACTIVE, MATCHED, DONATED, INACTIVE
    private boolean consentGiven;
    private boolean verified;

    @ServerTimestamp
    private Date createdAt;
    @ServerTimestamp
    private Date updatedAt;

    public DonorProfile() {
        // Required for Firestore
    }

    public String getDonorId() { return donorId; }
    public void setDonorId(String donorId) { this.donorId = donorId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }

    public String getMaskedName() {
        if (firstName == null || firstName.isEmpty()) return "Anonymous Donor";
        return firstName.charAt(0) + "*** " + (lastName != null && !lastName.isEmpty() ? lastName.charAt(0) + "***" : "");
    }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getGeneralLocation() { return generalLocation; }
    public void setGeneralLocation(String generalLocation) { this.generalLocation = generalLocation; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public List<String> getOrgansWillingToDonate() { return organsWillingToDonate; }
    public void setOrgansWillingToDonate(List<String> organsWillingToDonate) { this.organsWillingToDonate = organsWillingToDonate; }

    public String getDonationStatus() { return donationStatus; }
    public void setDonationStatus(String donationStatus) { this.donationStatus = donationStatus; }

    public boolean isConsentGiven() { return consentGiven; }
    public void setConsentGiven(boolean consentGiven) { this.consentGiven = consentGiven; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
