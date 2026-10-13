package com.aivideochat.controller;

import com.aivideochat.entity.AiProfile;
import com.aivideochat.entity.User;
import com.aivideochat.repository.AiProfileRepository;
import com.aivideochat.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.aivideochat.service.ChatReplyService;

import java.util.Map;

@Controller
public class ChatController {

    private final AiProfileRepository aiProfileRepository;
    private final UserRepository userRepository;
    private final ChatReplyService chatReplyService;

    public ChatController(
            AiProfileRepository aiProfileRepository,
            UserRepository userRepository,ChatReplyService chatReplyService) {

        this.aiProfileRepository = aiProfileRepository;
        this.userRepository = userRepository;
        this.chatReplyService = chatReplyService;

        
    }

    @GetMapping("/chat/{id}")
    public String showChat(
            @PathVariable Long id,
            Model model) {

        AiProfile profile =
                aiProfileRepository.findById(id).orElse(null);

        if (profile == null) {
            return "redirect:/home";
        }

        model.addAttribute("profile", profile);

        return "chat";
    }


    // 💬 SEND CHAT MESSAGE
    @PostMapping("/api/chat/send")
    @ResponseBody
    public ResponseEntity<?> sendChatMessage(
            @RequestParam Long profileId,
            @RequestParam String message,
            HttpSession session) {

        // 👤 Get logged-in user
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


        // 👤 Find user
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


        // 🤖 Check AI profile
        AiProfile profile =
                aiProfileRepository.findById(profileId).orElse(null);

        if (profile == null) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "error", "AI profile not found"
                    )
            );
        }


        // 📝 Validate message
        if (message == null ||
                message.trim().isEmpty()) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "error", "Message cannot be empty"
                    )
            );
        }


        // 🪙 CHAT COST
        int chatCost = 100;


        // 🪙 Check balance
        if (user.getCoins() < chatCost) {

            return ResponseEntity.ok(
                    Map.of(
                            "success", false,
                            "error", "Not enough coins",
                            "coins", user.getCoins()
                    )
            );
        }


        // 🪙 Deduct 100 coins
        user.setCoins(user.getCoins() - chatCost);
        userRepository.save(user);

        // Generate hardcoded Hinglish reply
        String aiReply = chatReplyService.getRandomReply();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "coins", user.getCoins(),
                        "message", "Message sent",
                        "chatCost", chatCost,
                        "reply", aiReply
                )
        );
    }
}