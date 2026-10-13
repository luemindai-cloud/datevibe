package com.aivideochat.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "gifts")
public class Gift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String icon;

    private int coins;

    public Gift() {
    }

    public Gift(String name, String icon, int coins) {
        this.name = name;
        this.icon = icon;
        this.coins = coins;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public int getCoins() {
        return coins;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }
}
