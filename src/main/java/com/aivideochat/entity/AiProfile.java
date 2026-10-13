package com.aivideochat.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class AiProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int age;

    private String description;

    private String personality;

    private String profileImage;
    
    private String videoFile;

    private boolean online;

    public AiProfile() {
    }

    public AiProfile(String name, int age, String description,
                     String personality, String profileImage,
                     boolean online) {

        this.name = name;
        this.age = age;
        this.description = description;
        this.personality = personality;
        this.profileImage = profileImage;
        this.online = online;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getDescription() {
        return description;
    }

    public String getPersonality() {
        return personality;
    }

    public String getProfileImage() {
        return profileImage;
    }
    
    public String getVideoFile() {
        return videoFile;
    }

    public boolean isOnline() {
        return online;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
    
    public void setVideoFile(String videoFile) {
        this.videoFile = videoFile;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}