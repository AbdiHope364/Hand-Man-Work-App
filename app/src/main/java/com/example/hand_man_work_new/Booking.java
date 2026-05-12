package com.example.hand_man_work_new;

import java.util.Date;

public class Booking {
    private String id;
    private String customerId;
    private String workerId;
    private Date dateTime;
    private String description;
    private String location;
    private String status;
    private Date createdAt;

    // Required empty constructor
    public Booking() {}

    public Booking(String customerId, String workerId, Date dateTime, 
                   String description, String location) {
        this.customerId = customerId;
        this.workerId = workerId;
        this.dateTime = dateTime;
        this.description = description;
        this.location = location;
        this.status = "pending";
        this.createdAt = new Date();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getWorkerId() { return workerId; }
    public void setWorkerId(String workerId) { this.workerId = workerId; }

    public Date getDateTime() { return dateTime; }
    public void setDateTime(Date dateTime) { this.dateTime = dateTime; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
