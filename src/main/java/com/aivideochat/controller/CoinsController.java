package com.aivideochat.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aivideochat.entity.User;
import com.aivideochat.repository.UserRepository;

@Controller
public class CoinsController {

    private final UserRepository userRepository;

    public CoinsController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/coins")
    public String showCoins() {
        return "coins";
    }

    @PostMapping("/api/coins/add")
    @ResponseBody
    public Map<String, Object> addCoins(@RequestBody Map<String, Integer> data) {

        Long userId = data.get("userId").longValue();
        int amount = data.get("coins");

        if (amount <= 0) {
            return Map.of("success", false, "message", "Invalid coin amount");
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return Map.of("success", false, "message", "User not found");
        }

        user.setCoins(user.getCoins() + amount);
        userRepository.save(user);

        return Map.of(
            "success", true,
            "message", "Coins added successfully",
            "newBalance", user.getCoins()
        );
    }
}