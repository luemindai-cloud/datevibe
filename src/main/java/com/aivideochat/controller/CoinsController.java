package com.aivideochat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CoinsController {

    @GetMapping("/coins")
    public String showCoins() {
        return "coins";
    }
}
