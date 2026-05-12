package com.example.hand_man_work_new;

public class Worker {
    private String name;
    private String imageUrl;
    private String uid;

    public Worker() {} // Required for Firebase

    public Worker(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
}
