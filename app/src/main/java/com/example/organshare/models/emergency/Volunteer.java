package com.example.organshare.models.emergency;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Volunteer implements Serializable {
    private String volunteerId;
    private String name;
    private String phone;
    private String location;
    private List<String> skills = new ArrayList<>();
    private String availabilityStatus; // AVAILABLE, ASSIGNED, UNAVAILABLE, OFFLINE
    private String assignedHospitalId;
    private String assignedHospitalName;
    private String assignedEmergencyRequestId;
    private String currentAssignment;

    @ServerTimestamp
    private Date lastUpdated;

    public Volunteer() {}

    public Volunteer(String volunteerId, String name, String phone, String location, List<String> skills,
                     String availabilityStatus, String assignedHospitalId) {
        this.volunteerId = volunteerId;
        this.name = name;
        this.phone = phone;
        this.location = location;
        if (skills != null) this.skills = skills;
        this.availabilityStatus = availabilityStatus;
        this.assignedHospitalId = assignedHospitalId;
    }

    public String getVolunteerId() { return volunteerId; }
    public void setVolunteerId(String volunteerId) { this.volunteerId = volunteerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }

    public String getAssignedHospitalId() { return assignedHospitalId; }
    public void setAssignedHospitalId(String assignedHospitalId) { this.assignedHospitalId = assignedHospitalId; }

    public String getAssignedHospitalName() { return assignedHospitalName; }
    public void setAssignedHospitalName(String assignedHospitalName) { this.assignedHospitalName = assignedHospitalName; }

    public String getAssignedEmergencyRequestId() { return assignedEmergencyRequestId; }
    public void setAssignedEmergencyRequestId(String assignedEmergencyRequestId) { this.assignedEmergencyRequestId = assignedEmergencyRequestId; }

    public String getCurrentAssignment() { return currentAssignment; }
    public void setCurrentAssignment(String currentAssignment) { this.currentAssignment = currentAssignment; }

    @com.google.firebase.firestore.Exclude
    public String getFullName() { return getName(); }
    @com.google.firebase.firestore.Exclude
    public void setFullName(String fullName) { setName(fullName); }

    @com.google.firebase.firestore.Exclude
    public String getRoleOrSpecialty() {
        if (skills != null && !skills.isEmpty()) return skills.get(0);
        return "General Medic";
    }
    @com.google.firebase.firestore.Exclude
    public void setRoleOrSpecialty(String role) {
        if (skills == null) skills = new ArrayList<>();
        skills.clear();
        skills.add(role);
    }

    @com.google.firebase.firestore.Exclude
    public String getCity() { return getLocation(); }
    @com.google.firebase.firestore.Exclude
    public void setCity(String city) { setLocation(city); }

    @com.google.firebase.firestore.Exclude
    public boolean isActive() {
        return !"OFFLINE".equalsIgnoreCase(availabilityStatus) && !"UNAVAILABLE".equalsIgnoreCase(availabilityStatus);
    }
    @com.google.firebase.firestore.Exclude
    public void setActive(boolean active) {
        this.availabilityStatus = active ? "AVAILABLE" : "OFFLINE";
    }

    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }
}
