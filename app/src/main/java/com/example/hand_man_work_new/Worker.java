package com.example.hand_man_work_new;

import java.util.List;

public class Worker {
    private String uid;
    private String name;
    private String imageUrl;
    private String bio;
    private List<String> skills;

    public Worker() {} 

    public Worker(String uid, String name, String imageUrl, String bio, List<String> skills) {
        this.uid = uid;
        this.name = name;
        this.imageUrl = imageUrl;
        this.bio = bio;
        this.skills = skills;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public String getBio() { return bio; }
    public List<String> getSkills() { return skills; }
}
