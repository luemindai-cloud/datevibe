package com.aivideochat.controller;

import com.aivideochat.entity.User;
import com.aivideochat.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/spin")
public class SpinController {

    private static final int SPIN_COST = 100;

    private final UserRepository userRepository;

    public SpinController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> spin(HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "error", "User is not logged in"
                    )
            );
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "error", "User not found"
                    )
            );
        }

        /*
         * Check whether the user has a free spin.
         */
        Boolean freeSpin =
                (Boolean) session.getAttribute("freeSpin");

        boolean usingFreeSpin =
                Boolean.TRUE.equals(freeSpin);

        /*
         * Normal spin requires 100 coins.
         */
        if (!usingFreeSpin && user.getCoins() < SPIN_COST) {

            return ResponseEntity.ok(
                    Map.of(
                            "success", false,
                            "notEnoughCoins", true,
                            "coins", user.getCoins(),
                            "requiredCoins", SPIN_COST
                    )
            );
        }

        /*
         * Deduct 100 coins only for a paid spin.
         */
        if (!usingFreeSpin) {
            user.setCoins(user.getCoins() - SPIN_COST);
        }

        /*
         * Consume the free spin if one was being used.
         */
        if (usingFreeSpin) {
            session.removeAttribute("freeSpin");
        }

        /*
         * ACTUAL RESULT:
         *
         * Only two outcomes are possible.
         *
         * 0 = Better Luck Next Time
         * 1 = One More Spin
         */
        boolean extraSpin =
                ThreadLocalRandom.current().nextBoolean();

        String result;

        if (extraSpin) {

            result = "EXTRA_SPIN";

            /*
             * Give exactly ONE free spin.
             */
            session.setAttribute("freeSpin", true);

        } else {

            result = "BETTER_LUCK";
        }

        userRepository.save(user);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "result", result,
                        "coins", user.getCoins(),
                        "freeSpin", extraSpin
                )
        );
    }
}