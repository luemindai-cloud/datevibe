package com.aivideochat.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long profileId;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(nullable = false)
    private String sender;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ChatMessage() {
    }

    public ChatMessage(Long userId, Long profileId,
                       String message, String sender) {
        this.userId = userId;
        this.profileId = profileId;
        this.message = message;
        this.sender = sender;
        this.createdAt = LocalDateTime.now();
        this.read = false;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getProfileId() {
        return profileId;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public boolean isRead() {
        return read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}