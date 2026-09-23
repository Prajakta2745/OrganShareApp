package com.example.organshare.models;

import java.io.Serializable;

public class Checkpoint implements Serializable {
    private String checkpointId;
    private String deliveryId;
    private String locationName;
    private double latitude;
    private double longitude;
    private String timestamp;
    private String statusAtCheckpoint;
    private String notes;

    public Checkpoint() {}

    public Checkpoint(String checkpointId, String deliveryId, String locationName, double latitude, double longitude, String timestamp, String statusAtCheckpoint, String notes) {
        this.checkpointId = checkpointId;
        this.deliveryId = deliveryId;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.statusAtCheckpoint = statusAtCheckpoint;
        this.notes = notes;
    }

    public String getCheckpointId() { return checkpointId; }
    public void setCheckpointId(String checkpointId) { this.checkpointId = checkpointId; }

    public String getDeliveryId() { return deliveryId; }
    public void setDeliveryId(String deliveryId) { this.deliveryId = deliveryId; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getStatusAtCheckpoint() { return statusAtCheckpoint; }
    public void setStatusAtCheckpoint(String statusAtCheckpoint) { this.statusAtCheckpoint = statusAtCheckpoint; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
