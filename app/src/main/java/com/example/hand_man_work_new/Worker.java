package com.example.hand_man_work_new;

import java.util.List;

public class Worker {
    private String uid;
    private String name;
    private String imageUrl;
    private String category;
    private boolean isVerified; // New Security Field

    public Worker() {} 

    public Worker(String uid, String name, String imageUrl, String category) {
        this.uid = uid;
        this.name = name;
        this.imageUrl = imageUrl;
        this.category = category;
        this.isVerified = false; // Default to false until Admin checks
    }

    public String getUid() { return uid; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public String getCategory() { return category; }
    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }
}
