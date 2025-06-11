package com.example.shiftixapp;

import java.util.Date;

public class Shift {
    private String shiftId;
    private String userId;
    private String department;
    private Date startTime;
    private Date endTime;
    private String username;

    // Default constructor (required for Firestore)
    public Shift() {
    }

    public Shift(String shiftId, String userId, String department, Date startTime, Date endTime, String username) {
        this.shiftId = shiftId;
        this.userId = userId;
        this.department = department;
        this.startTime = startTime;
        this.endTime = endTime;
        this.username = username;
    }

    // Getters and setters
    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
