package com.aivideochat.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "video_call_sessions")
public class VideoCallSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long profileId;

    private LocalDateTime startedAt;

    private int chargedMinutes;

    private boolean active;

    public VideoCallSession() {
    }

    public VideoCallSession(
            Long userId,
            Long profileId) {

        this.userId = userId;
        this.profileId = profileId;
        this.startedAt = LocalDateTime.now();
        this.chargedMinutes = 1;
        this.active = true;
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

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public int getChargedMinutes() {
        return chargedMinutes;
    }

    public boolean isActive() {
        return active;
    }

    public void setChargedMinutes(int chargedMinutes) {
        this.chargedMinutes = chargedMinutes;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
