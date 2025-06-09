package com.example.shiftixapp;

import java.util.Date;

public class SwapRequest {
    private String requestId;
    private String shiftFromId;
    private String shiftToId;
    private String status; // e.g., "Pending", "Approved", "Rejected"
    private Date createdAt;

    // Empty constructor for Firestore
    public SwapRequest() {}

    public SwapRequest(String requestId, String shiftFromId, String shiftToId, String status, Date createdAt) {
        this.requestId = requestId;
        this.shiftFromId = shiftFromId;
        this.shiftToId = shiftToId;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public String getShiftFromId() { return shiftFromId; }
    public void setShiftFromId(String shiftFromId) { this.shiftFromId = shiftFromId; }
    public String getShiftToId() { return shiftToId; }
    public void setShiftToId(String shiftToId) { this.shiftToId = shiftToId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}