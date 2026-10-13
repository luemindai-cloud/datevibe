package com.aivideochat.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gift_transactions")
public class GiftTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long profileId;

    private Long giftId;

    private String giftName;

    private String giftIcon;

    private int coins;

    private LocalDateTime createdAt;

    public GiftTransaction() {
    }

    public GiftTransaction(
            Long userId,
            Long profileId,
            Long giftId,
            String giftName,
            String giftIcon,
            int coins) {

        this.userId = userId;
        this.profileId = profileId;
        this.giftId = giftId;
        this.giftName = giftName;
        this.giftIcon = giftIcon;
        this.coins = coins;
        this.createdAt = LocalDateTime.now();
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

    public Long getGiftId() {
        return giftId;
    }

    public String getGiftName() {
        return giftName;
    }

    public String getGiftIcon() {
        return giftIcon;
    }

    public int getCoins() {
        return coins;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}