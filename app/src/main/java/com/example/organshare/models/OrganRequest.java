package com.example.organshare.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class OrganRequest implements Serializable {

    private String requestId;

    private String hospitalId;
    private String hospitalName;
    private String hospitalAddress;
    private String hospitalContact;

    private String organRequired;
    private String bloodGroup;

    private int quantity = 1;
    private int requestedQuantity = 1;
    private int fulfilledQuantity = 0;
    private int remainingQuantity = 1;

    private String emergencyLevel;

    private String patientRefId;

    private String requiredDate;
    private String requiredTime;

    private String additionalNotes;

    /*
     * Possible values:
     * PENDING
     * UNDER_REVIEW
     * APPROVED
     * ACTIVE
     * PARTIALLY_FULFILLED
     * FULFILLED
     * REJECTED
     * CANCELLED
     */
    private String status;

    // Alias for status
    private String requestStatus;

    private String matchedInventoryId;
    private String allocatedOrderId;

    private String compatibilitySummary;

    private String approvedAt;
    private String fulfilledAt;

    @ServerTimestamp
    private Date createdAt;

    @ServerTimestamp
    private Date updatedAt;

    // Required empty constructor for Firestore
    public OrganRequest() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getHospitalAddress() {
        return hospitalAddress;
    }

    public void setHospitalAddress(String hospitalAddress) {
        this.hospitalAddress = hospitalAddress;
    }

    public String getHospitalContact() {
        return hospitalContact;
    }

    public void setHospitalContact(String hospitalContact) {
        this.hospitalContact = hospitalContact;
    }

    public String getOrganRequired() {
        return organRequired;
    }

    public void setOrganRequired(String organRequired) {
        this.organRequired = organRequired;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {

        this.quantity = quantity;

        if (this.requestedQuantity <= 0) {
            this.requestedQuantity = quantity;
        }

        this.remainingQuantity =
                Math.max(
                        0,
                        this.requestedQuantity
                                - this.fulfilledQuantity
                );
    }

    public int getRequestedQuantity() {

        return requestedQuantity > 0
                ? requestedQuantity
                : (quantity > 0 ? quantity : 1);
    }

    public void setRequestedQuantity(int requestedQuantity) {

        this.requestedQuantity = requestedQuantity;
        this.quantity = requestedQuantity;

        this.remainingQuantity =
                Math.max(
                        0,
                        requestedQuantity
                                - this.fulfilledQuantity
                );
    }

    public int getFulfilledQuantity() {
        return fulfilledQuantity;
    }

    public void setFulfilledQuantity(int fulfilledQuantity) {

        this.fulfilledQuantity = fulfilledQuantity;

        this.remainingQuantity =
                Math.max(
                        0,
                        getRequestedQuantity()
                                - fulfilledQuantity
                );
    }

    public int getRemainingQuantity() {

        return Math.max(
                0,
                getRequestedQuantity()
                        - fulfilledQuantity
        );
    }

    public void setRemainingQuantity(int remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public String getEmergencyLevel() {
        return emergencyLevel;
    }

    public void setEmergencyLevel(String emergencyLevel) {
        this.emergencyLevel = emergencyLevel;
    }

    public String getPatientRefId() {
        return patientRefId;
    }

    public void setPatientRefId(String patientRefId) {
        this.patientRefId = patientRefId;
    }

    public String getRequiredDate() {
        return requiredDate;
    }

    public void setRequiredDate(String requiredDate) {
        this.requiredDate = requiredDate;
    }

    public String getRequiredTime() {
        return requiredTime;
    }

    public void setRequiredTime(String requiredTime) {
        this.requiredTime = requiredTime;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    public String getStatus() {

        return status != null
                ? status
                : requestStatus;
    }

    public void setStatus(String status) {

        this.status = status;
        this.requestStatus = status;
    }

    public String getRequestStatus() {
        return getStatus();
    }

    public void setRequestStatus(String requestStatus) {
        setStatus(requestStatus);
    }

    public String getMatchedInventoryId() {
        return matchedInventoryId;
    }

    public void setMatchedInventoryId(
            String matchedInventoryId
    ) {
        this.matchedInventoryId = matchedInventoryId;
    }

    public String getAllocatedOrderId() {
        return allocatedOrderId;
    }

    public void setAllocatedOrderId(
            String allocatedOrderId
    ) {
        this.allocatedOrderId = allocatedOrderId;
    }

    public String getCompatibilitySummary() {
        return compatibilitySummary;
    }

    public void setCompatibilitySummary(
            String compatibilitySummary
    ) {
        this.compatibilitySummary = compatibilitySummary;
    }

    public String getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(String approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getFulfilledAt() {
        return fulfilledAt;
    }

    public void setFulfilledAt(String fulfilledAt) {
        this.fulfilledAt = fulfilledAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}