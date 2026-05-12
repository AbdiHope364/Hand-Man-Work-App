package com.example.hand_man_work_new;

public class Booking {
    private String bookingId;
    private String customerId;
    private String workerId;
    private String customerName;
    private String status; // pending, accepted, rejected, completed
    private long timestamp;

    public Booking() {} // Required for Firestore

    public Booking(String bookingId, String customerId, String customerName, String workerId) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.workerId = workerId;
        this.status = "pending";
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getWorkerId() { return workerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getTimestamp() { return timestamp; }
}
