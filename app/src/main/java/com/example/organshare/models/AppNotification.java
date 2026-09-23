package com.example.organshare.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;

public class AppNotification implements Serializable {
    private String notificationId;
    private String recipientUserId;
    private String recipientRole; // ALL, DONOR, HOSPITAL, ORGAN_BANK, DELIVERY, ADMIN
    private String title;
    private String message;
    private String type; // REQUEST, ALLOCATION, DELIVERY, DELAY, VERIFICATION, SYSTEM
    private String referenceId; // requestId, orderId, deliveryId, etc.
    private boolean isRead;

    @ServerTimestamp
    private Date timestamp;

    public AppNotification() {}

    public AppNotification(String notificationId, String recipientUserId, String recipientRole, String title, String message, String type, String referenceId) {
        this.notificationId = notificationId;
        this.recipientUserId = recipientUserId;
        this.recipientRole = recipientRole;
        this.title = title;
        this.message = message;
        this.type = type;
        this.referenceId = referenceId;
        this.isRead = false;
    }

    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }

    public String getRecipientUserId() { return recipientUserId; }
    public void setRecipientUserId(String recipientUserId) { this.recipientUserId = recipientUserId; }

    public String getRecipientRole() { return recipientRole; }
    public void setRecipientRole(String recipientRole) { this.recipientRole = recipientRole; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}
