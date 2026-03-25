package com.example.hand_man_work;

import java.util.List;

public class Worker {
    private String uid;
    private String name;
    private List<String> skills;
    private double hourlyRate;
    private String phone;
    private String type;
    private String photoUrl;

    // Required empty constructor for Firestore
    public Worker() {}

    public Worker(String uid, String name, List<String> skills, double hourlyRate, String photoUrl) {
        this.uid = uid;
        this.name = name;
        this.skills = skills;
        this.hourlyRate = hourlyRate;
        this.type = "worker";
        this.photoUrl = photoUrl;
    }

    // Getters and Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    
    // Helper method to display skills as string
    public String getSkillsString() {
        if (skills == null || skills.isEmpty()) return "No skills listed";
        return String.join(", ", skills);
    }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}
