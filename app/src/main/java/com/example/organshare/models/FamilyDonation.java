package com.example.organshare.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FamilyDonation implements Serializable {
    private String donationId; // e.g. FMD-2026-XXXX
    private String deceasedName;
    private String deathDateTime;
    private String hospitalName;
    private String hospitalLocation;
    private String familyMemberName;
    private String relationship;
    private String contactPhone;
    private String bloodGroup;
    private List<String> selectedOrgans = new ArrayList<>();
    private String consentStatus; // CONSENT_ATTACHED, VERBAL_CONFIRMED, PENDING_DOCS
    private String verificationStatus; // SUBMITTED, UNDER_VERIFICATION, VERIFIED, REJECTED, COMPLETED
    private String registeredByUserId;
    private String notes;
    private String verifiedByCoordinator;

    @ServerTimestamp
    private Date createdAt;
    @ServerTimestamp
    private Date updatedAt;

    public FamilyDonation() {}

    public String getDonationId() { return donationId; }
    public void setDonationId(String donationId) { this.donationId = donationId; }

    public String getDeceasedName() { return deceasedName; }
    public void setDeceasedName(String deceasedName) { this.deceasedName = deceasedName; }

    public String getDeathDateTime() { return deathDateTime; }
    public void setDeathDateTime(String deathDateTime) { this.deathDateTime = deathDateTime; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public String getHospitalLocation() { return hospitalLocation; }
    public void setHospitalLocation(String hospitalLocation) { this.hospitalLocation = hospitalLocation; }

    public String getFamilyMemberName() { return familyMemberName; }
    public void setFamilyMemberName(String familyMemberName) { this.familyMemberName = familyMemberName; }

    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public List<String> getSelectedOrgans() { return selectedOrgans; }
    public void setSelectedOrgans(List<String> selectedOrgans) { this.selectedOrgans = selectedOrgans; }

    public String getConsentStatus() { return consentStatus; }
    public void setConsentStatus(String consentStatus) { this.consentStatus = consentStatus; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }

    public String getRegisteredByUserId() { return registeredByUserId; }
    public void setRegisteredByUserId(String registeredByUserId) { this.registeredByUserId = registeredByUserId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getVerifiedByCoordinator() { return verifiedByCoordinator; }
    public void setVerifiedByCoordinator(String verifiedByCoordinator) { this.verifiedByCoordinator = verifiedByCoordinator; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
