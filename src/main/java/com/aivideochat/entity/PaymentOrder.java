package com.aivideochat.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_orders")
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String orderId;

    private int amount;

    private int coins;

    private String status;

    private LocalDateTime createdAt;

    public PaymentOrder() {
    }

    public PaymentOrder(Long userId, String orderId, int amount, int coins) {
        this.userId = userId;
        this.orderId = orderId;
        this.amount = amount;
        this.coins = coins;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public int getAmount() {
        return amount;
    }

    public int getCoins() {
        return coins;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}