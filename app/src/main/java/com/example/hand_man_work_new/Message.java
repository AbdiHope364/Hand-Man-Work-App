package com.example.hand_man_work_new;

public class Message {
    private String senderId;
    private String message;
    private long timestamp;

    public Message() {} // Required for Firebase

    public Message(String senderId, String message) {
        this.senderId = senderId;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public String getSenderId() { return senderId; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
}
