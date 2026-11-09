package com.aivideochat.controller;

import com.aivideochat.entity.AiProfile;
import com.aivideochat.entity.ChatMessage;
import com.aivideochat.entity.User;
import com.aivideochat.repository.AiProfileRepository;
import com.aivideochat.repository.ChatMessageRepository;
import com.aivideochat.repository.UserRepository;
import com.aivideochat.service.ChatReplyService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class ChatController {

    private final AiProfileRepository aiProfileRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatReplyService chatReplyService;

    public ChatController(
            AiProfileRepository aiProfileRepository,
            UserRepository userRepository,
            ChatMessageRepository chatMessageRepository,
            ChatReplyService chatReplyService) {

        this.aiProfileRepository = aiProfileRepository;
        this.userRepository = userRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.chatReplyService = chatReplyService;
    }

    @GetMapping("/chat/{id}")
    public String showChat(
            @PathVariable("id") Long id,
            Model model,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/quick-login";
        }

        AiProfile profile =
                aiProfileRepository.findById(id).orElse(null);

        if (profile == null) {
            return "redirect:/home";
        }

        List<ChatMessage> chatHistory =
                chatMessageRepository
                        .findByUserIdAndProfileIdOrderByCreatedAtAsc(
                                userId, id);

        model.addAttribute("profile", profile);
        model.addAttribute("chatHistory", chatHistory);

        return "chat";
    }

    @PostMapping("/api/chat/send")
    @ResponseBody
    public ResponseEntity<?> sendChatMessage(
            @RequestParam("profileId") Long profileId,
            @RequestParam("message") String message,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false,
                           "error", "User is not logged in"));
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false,
                           "error", "User not found"));
        }

        AiProfile profile =
                aiProfileRepository.findById(profileId).orElse(null);

        if (profile == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false,
                           "error", "AI profile not found"));
        }

        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false,
                           "error", "Message cannot be empty"));
        }

        int chatCost = 100;

        if (user.getCoins() < chatCost) {
            return ResponseEntity.ok(
                    Map.of("success", false,
                           "error", "Not enough coins",
                           "coins", user.getCoins()));
        }

        // Deduct coins for user message
        user.setCoins(user.getCoins() - chatCost);
        userRepository.save(user);

        // Save user message
        ChatMessage userMessage = new ChatMessage(
                userId, profileId, message.trim(), "USER");

        chatMessageRepository.save(userMessage);

        // Generate AI reply
        String aiReply = chatReplyService.getRandomReply();

        // Save AI reply
        ChatMessage aiMessage = new ChatMessage(
                userId, profileId, aiReply, "AI");

        chatMessageRepository.save(aiMessage);

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