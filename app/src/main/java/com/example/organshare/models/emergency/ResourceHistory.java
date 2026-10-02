package com.example.organshare.models.emergency;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;

public class ResourceHistory implements Serializable {
    private String historyId;
    private String resourceType; // BEDS, BLOOD, TRANSPORT, VOLUNTEERS, SUPPLIES
    private String resourceId;
    private String resourceName;
    private String previousValue;
    private String newValue;
    private String action; // ALLOCATED, DEALLOCATED, QUANTITY_UPDATED, STATUS_CHANGED, CREATED
    private String performedBy;
    private String performedByName;
    private String emergencyRequestId;
    private String destination;
    private String notes;

    @ServerTimestamp
    private Date timestamp;

    public ResourceHistory() {}

    public ResourceHistory(String historyId, String resourceType, String resourceId, String resourceName,
                           String previousValue, String newValue, String action, String performedBy,
                           String performedByName, String emergencyRequestId, String destination, String notes) {
        this.historyId = historyId;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.previousValue = previousValue;
        this.newValue = newValue;
        this.action = action;
        this.performedBy = performedBy;
        this.performedByName = performedByName;
        this.emergencyRequestId = emergencyRequestId;
        this.destination = destination;
        this.notes = notes;
    }

    public String getHistoryId() { return historyId; }
    public void setHistoryId(String historyId) { this.historyId = historyId; }

    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }

    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }

    public String getPreviousValue() { return previousValue; }
    public void setPreviousValue(String previousValue) { this.previousValue = previousValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public String getPerformedByName() { return performedByName; }
    public void setPerformedByName(String performedByName) { this.performedByName = performedByName; }

    public String getEmergencyRequestId() { return emergencyRequestId; }
    public void setEmergencyRequestId(String emergencyRequestId) { this.emergencyRequestId = emergencyRequestId; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}
