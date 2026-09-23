package com.example.organshare.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;

public class UserModel implements Serializable {
    private String uid;
    private String email;
    private String displayName;
    private String role; // DONOR, HOSPITAL, ORGAN_BANK, DELIVERY, ADMIN
    private String phone;
    private boolean verified;
    private boolean suspended;
    
    @ServerTimestamp
    private Date createdAt;
    @ServerTimestamp
    private Date updatedAt;

    public UserModel() {
        // Required for Firestore serialization
    }

    public UserModel(String uid, String email, String displayName, String role, String phone) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
        this.role = role;
        this.phone = phone;
        this.verified = role.equals("DONOR") || role.equals("ADMIN"); // Admin/Donor auto-verify or pending admin review for hospital/bank
        this.suspended = false;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public boolean isSuspended() { return suspended; }
    public void setSuspended(boolean suspended) { this.suspended = suspended; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
