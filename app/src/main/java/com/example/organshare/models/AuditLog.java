package com.example.organshare.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;

public class AuditLog implements Serializable {
    private String logId;
    private String userId;
    private String userName;
    private String userRole;
    private String action; // e.g. "APPROVED_ORGAN_REQUEST", "ALLOCATED_INVENTORY", "STARTED_DELIVERY"
    private String targetEntityType; // REQUEST, INVENTORY, ORDER, DELIVERY, USER
    private String targetEntityId;
    private String details;

    @ServerTimestamp
    private Date timestamp;

    public AuditLog() {}

    public AuditLog(String logId, String userId, String userName, String userRole, String action, String targetEntityType, String targetEntityId, String details) {
        this.logId = logId;
        this.userId = userId;
        this.userName = userName;
        this.userRole = userRole;
        this.action = action;
        this.targetEntityType = targetEntityType;
        this.targetEntityId = targetEntityId;
        this.details = details;
    }

    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getTargetEntityType() { return targetEntityType; }
    public void setTargetEntityType(String targetEntityType) { this.targetEntityType = targetEntityType; }

    public String getTargetEntityId() { return targetEntityId; }
    public void setTargetEntityId(String targetEntityId) { this.targetEntityId = targetEntityId; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}
