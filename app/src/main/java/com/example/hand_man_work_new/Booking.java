package com.example.hand_man_work_new;

public class Booking {
    private String bookingId;
    private String customerId;
    private String workerId;
    private String status; // pending, accepted, completed, cancelled
    private long timestamp;

    public Booking() {} // Required for Firestore

    public Booking(String customerId, String workerId) {
        this.customerId = customerId;
        this.workerId = workerId;
        this.status = "pending";
        this.timestamp = System.currentTimeMillis();
    }

    // Add Getters and Setters here
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getWorkerId() { return workerId; }
}
