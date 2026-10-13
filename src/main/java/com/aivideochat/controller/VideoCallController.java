package com.aivideochat.controller;

import com.aivideochat.entity.AiProfile;
import com.aivideochat.repository.AiProfileRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.aivideochat.entity.User;
import com.aivideochat.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.Map;
import com.aivideochat.entity.VideoCallSession;
import com.aivideochat.repository.VideoCallSessionRepository;
import org.springframework.transaction.annotation.Transactional;


@Controller
public class VideoCallController {

    private final AiProfileRepository aiProfileRepository;
    private final UserRepository userRepository;
    private final VideoCallSessionRepository videoCallSessionRepository;

    public VideoCallController(
            AiProfileRepository aiProfileRepository,
            UserRepository userRepository,VideoCallSessionRepository videoCallSessionRepository) {

        this.aiProfileRepository = aiProfileRepository;
        this.userRepository = userRepository;
        this.videoCallSessionRepository = videoCallSessionRepository;

    }

    @GetMapping("/video-call/{id}")
    public String showVideoCall(
            @PathVariable Long id,
            Model model) {

        AiProfile profile =
                aiProfileRepository.findById(id)
                        .orElse(null);

        if (profile == null) {
            return "redirect:/home";
        }

        model.addAttribute("profile", profile);

        return "video-call";
    }
    
    @PostMapping("/api/video-call/start/{id}")
    @Transactional
    public ResponseEntity<?> startVideoCall(
            @PathVariable Long id,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false,
                           "error", "User is not logged in")
            );
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false,
                           "error", "User not found")
            );
        }

        AiProfile profile =
                aiProfileRepository.findById(id).orElse(null);

        if (profile == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false,
                           "error", "AI profile not found")
            );
        }

        int videoCallCost = 1500;

        if (user.getCoins() < videoCallCost) {
            return ResponseEntity.ok(
                    Map.of("success", false,
                           "error", "Not enough coins",
                           "coins", user.getCoins())
            );
        }

        user.setCoins(user.getCoins() - videoCallCost);
        userRepository.save(user);

        // Create video call session
        VideoCallSession callSession =
                new VideoCallSession(
                        userId,
                        profile.getId()
                );

        videoCallSessionRepository.save(callSession);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "coins", user.getCoins(),
                        "sessionId", callSession.getId(),
                        "message", "Video call started"
                )
        );    
    }
    
    @PostMapping("/api/video-call/minute/{sessionId}")
    @Transactional
    public ResponseEntity<?> chargeVideoCallMinute(
            @PathVariable Long sessionId,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "error", "User is not logged in"
                    )
            );
        }

        // Get the active session belonging to this user
        VideoCallSession callSession =
                videoCallSessionRepository
                        .findActiveSessionForUpdate(sessionId, userId)
                        .orElse(null);

        if (callSession == null) {
            return ResponseEntity.ok(
                    Map.of(
                            "success", false,
                            "error", "Video call session not found or already ended"
                    )
            );
        }

        User user =
                userRepository.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "error", "User not found"
                    )
            );
        }

        int videoCallCost = 1500;

        // Check whether another minute is actually due
        long elapsedSeconds =
                java.time.Duration.between(
                        callSession.getStartedAt(),
                        java.time.LocalDateTime.now()
                ).getSeconds();

        long elapsedMinutes = elapsedSeconds / 60;

        if (elapsedMinutes < callSession.getChargedMinutes()) {
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "charged", false,
                            "coins", user.getCoins(),
                            "message", "No new minute charge yet"
                    )
            );
        }

        // Not enough coins for the next minute
        if (user.getCoins() < videoCallCost) {

            callSession.setActive(false);
            videoCallSessionRepository.save(callSession);

            return ResponseEntity.ok(
                    Map.of(
                            "success", false,
                            "charged", false,
                            "coins", user.getCoins(),
                            "error", "Not enough coins",
                            "endCall", true
                    )
            );
        }

        // Charge another 1,500 coins
        user.setCoins(
                user.getCoins() - videoCallCost
        );

        userRepository.save(user);

        callSession.setChargedMinutes(
                callSession.getChargedMinutes() + 1
        );

        videoCallSessionRepository.save(callSession);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "charged", true,
                        "coins", user.getCoins(),
                        "chargedMinutes",
                        callSession.getChargedMinutes(),
                        "message",
                        "1,500 coins charged for another minute"
                )
        );
    }
    
}
