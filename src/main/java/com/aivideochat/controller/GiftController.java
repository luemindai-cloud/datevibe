package com.aivideochat.controller;

import com.aivideochat.entity.Gift;
import com.aivideochat.entity.GiftTransaction;
import com.aivideochat.entity.User;
import com.aivideochat.repository.GiftRepository;
import com.aivideochat.repository.GiftTransactionRepository;
import com.aivideochat.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gifts")
public class GiftController {

    private final GiftRepository giftRepository;
    private final GiftTransactionRepository giftTransactionRepository;
    private final UserRepository userRepository;

    public GiftController(
            GiftRepository giftRepository,
            GiftTransactionRepository giftTransactionRepository,
            UserRepository userRepository) {

        this.giftRepository = giftRepository;
        this.giftTransactionRepository = giftTransactionRepository;
        this.userRepository = userRepository;
    }

    // =========================
    // GET ALL GIFTS
    // =========================

    @GetMapping
    public List<Gift> getAllGifts() {
        return giftRepository.findAll();
    }

    // =========================
    // SEND GIFT
    // =========================

    @PostMapping("/send")
    @Transactional
    public ResponseEntity<?> sendGift(
            @RequestParam Long profileId,
            @RequestParam Long giftId,
            HttpSession session) {

        // Check login
        Long userId =
                (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "error", "User is not logged in"
                    )
            );
        }

        // Find user
        User user =
                userRepository.findById(userId)
                        .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "error", "User not found"
                    )
            );
        }

        // Find gift
        Gift gift =
                giftRepository.findById(giftId)
                        .orElse(null);

        if (gift == null) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "error", "Gift not found"
                    )
            );
        }

        // Check coins
        if (user.getCoins() < gift.getCoins()) {

            return ResponseEntity.ok(
                    Map.of(
                            "success", false,
                            "notEnoughCoins", true,
                            "coins", user.getCoins(),
                            "requiredCoins", gift.getCoins(),
                            "error", "Not enough coins"
                    )
            );
        }

        // Deduct coins
        user.setCoins(
                user.getCoins() - gift.getCoins()
        );

        userRepository.save(user);

        // Save transaction
        GiftTransaction transaction =
                new GiftTransaction(
                        userId,
                        profileId,
                        gift.getId(),
                        gift.getName(),
                        gift.getIcon(),
                        gift.getCoins()
                );

        giftTransactionRepository.save(transaction);

        // Success
        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "giftName", gift.getName(),
                        "giftIcon", gift.getIcon(),
                        "giftCoins", gift.getCoins(),
                        "coins", user.getCoins(),
                        "message",
                        "Gift sent successfully!"
                )
        );
    }
}