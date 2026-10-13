package com.aivideochat.controller;

import com.aivideochat.repository.AiProfileRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatsController {

    private final AiProfileRepository aiProfileRepository;

    public ChatsController(AiProfileRepository aiProfileRepository) {
        this.aiProfileRepository = aiProfileRepository;
    }

    @GetMapping("/chats")
    public String showChats(Model model) {

        model.addAttribute(
                "profiles",
                aiProfileRepository.findAll()
        );

        return "chats";
    }
}
