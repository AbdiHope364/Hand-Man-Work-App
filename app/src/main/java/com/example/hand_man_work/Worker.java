package com.example.hand_man_work;

import java.util.List;

public class Worker {
    private String uid;
    private String name;
    private List<String> skills;
    private double hourlyRate;
    private String phone;
    private String photoUrl;

    // Required empty constructor for Firestore
    public Worker() {}

    public Worker(String uid, String name, List<String> skills, double hourlyRate) {
        this.uid = uid;
        this.name = name;
        this.skills = skills;
        this.hourlyRate = hourlyRate;
    }

    public String getUid() { return uid; }
    public String getName() { return name; }
    public List<String> getSkills() { return skills; }
    public double getHourlyRate() { return hourlyRate; }
    public String getPhone() { return phone; }
    public String getPhotoUrl() { return photoUrl; }

    public String getSkillsString() {
        if (skills == null || skills.isEmpty()) return "No skills listed";
        return String.join(", ", skills);
    }
}
