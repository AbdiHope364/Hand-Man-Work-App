package com.example.hand_man_work_new;

import java.util.List;

public class Worker {
    private String uid;
    private String name;
    private String imageUrl;
    private String bio;
    private List<String> skills;
    private String category; // Plumbing, Electrical, etc.
    private String address;
    private double rating;
    private int reviewCount;

    public Worker() {} 

    public Worker(String uid, String name, String imageUrl, String category, String address) {
        this.uid = uid;
        this.name = name;
        this.imageUrl = imageUrl;
        this.category = category;
        this.address = address;
        this.rating = 0.0;
        this.reviewCount = 0;
    }

    // Getters and Setters
    public String getUid() { return uid; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public String getCategory() { return category; }
    public String getAddress() { return address; }
    public double getRating() { return rating; }
    public int getReviewCount() { return reviewCount; }
}
