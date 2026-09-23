package com.example.organshare.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;

public class Allocation implements Serializable {
    private String allocationId;
    private String requestId;
    private String hospitalId;
    private String hospitalName;
    private String inventoryId;
    private String organType;
    private String bloodGroup;
    private String coordinatorId;
    private String coordinatorName;
    private String organBankId;
    private String organBankName;
    private String allocationDate;
    private String allocationTime;
    private String orderId; // Generated ORD-2026-XXXXX
    private String status; // ALLOCATED, DISPATCHED, COMPLETED, CANCELLED

    @ServerTimestamp
    private Date createdAt;

    public Allocation() {}

    public String getAllocationId() { return allocationId; }
    public void setAllocationId(String allocationId) { this.allocationId = allocationId; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getHospitalId() { return hospitalId; }
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public String getInventoryId() { return inventoryId; }
    public void setInventoryId(String inventoryId) { this.inventoryId = inventoryId; }

    public String getOrganType() { return organType; }
    public void setOrganType(String organType) { this.organType = organType; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getCoordinatorId() { return coordinatorId; }
    public void setCoordinatorId(String coordinatorId) { this.coordinatorId = coordinatorId; }

    public String getCoordinatorName() { return coordinatorName; }
    public void setCoordinatorName(String coordinatorName) { this.coordinatorName = coordinatorName; }

    public String getOrganBankId() { return organBankId; }
    public void setOrganBankId(String organBankId) { this.organBankId = organBankId; }

    public String getOrganBankName() { return organBankName; }
    public void setOrganBankName(String organBankName) { this.organBankName = organBankName; }

    public String getAllocationDate() { return allocationDate; }
    public void setAllocationDate(String allocationDate) { this.allocationDate = allocationDate; }

    public String getAllocationTime() { return allocationTime; }
    public void setAllocationTime(String allocationTime) { this.allocationTime = allocationTime; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
