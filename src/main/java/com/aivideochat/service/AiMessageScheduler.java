package com.aivideochat.service;

import com.aivideochat.entity.AiProfile;
import com.aivideochat.entity.ChatMessage;
import com.aivideochat.entity.User;
import com.aivideochat.repository.AiProfileRepository;
import com.aivideochat.repository.ChatMessageRepository;
import com.aivideochat.repository.UserRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class AiMessageScheduler {

    private final UserRepository userRepository;
    private final AiProfileRepository aiProfileRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatReplyService chatReplyService;

    public AiMessageScheduler(
            UserRepository userRepository,
            AiProfileRepository aiProfileRepository,
            ChatMessageRepository chatMessageRepository,
            ChatReplyService chatReplyService) {

        this.userRepository = userRepository;
        this.aiProfileRepository = aiProfileRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.chatReplyService = chatReplyService;
    }

    // Runs every 5 minutes
    @Scheduled(fixedDelay = 300000)
    public void sendRandomMessages() {

        List<User> users = userRepository.findAll();
        List<AiProfile> profiles = aiProfileRepository.findAll();

        if (users.isEmpty() || profiles.isEmpty()) {
            return;
        }

        // Select a random user and AI profile
        User user = users.get(
                ThreadLocalRandom.current().nextInt(users.size()));

        AiProfile profile = profiles.get(
                ThreadLocalRandom.current().nextInt(profiles.size()));

        // Generate an incoming AI message
        String message = chatReplyService.getRandomIncomingMessage();

        ChatMessage incomingMessage = new ChatMessage(
                user.getId(),
                profile.getId(),
                message,
                "AI"
        );

        // Save message as unread
        incomingMessage.setRead(false);

        chatMessageRepository.save(incomingMessage);

        System.out.println(
                "DateVibe: AI message sent to user "
                + user.getId()
                + " from "
                + profile.getName());
    }
}