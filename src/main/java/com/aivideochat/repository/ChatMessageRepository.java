package com.aivideochat.repository;

import com.aivideochat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository
        extends JpaRepository<ChatMessage, Long> {

    // Load conversation history
    List<ChatMessage> findByUserIdAndProfileIdOrderByCreatedAtAsc(
            Long userId, Long profileId);

 // Load unread incoming AI messages
    List<ChatMessage> findByUserIdAndSenderAndReadFalseOrderByCreatedAtDesc(
            Long userId, String sender);

    // Count unread incoming AI messages
    long countByUserIdAndSenderAndReadFalse(
            Long userId, String sender);
}