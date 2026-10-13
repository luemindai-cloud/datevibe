package com.aivideochat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.aivideochat.entity.User;
import com.aivideochat.repository.UserRepository;
import com.aivideochat.repository.AiProfileRepository;
import jakarta.servlet.http.HttpSession;

import java.util.Map;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class UserController {

    private final UserRepository userRepository;
    private final AiProfileRepository aiProfileRepository;

    public UserController(
            UserRepository userRepository,
            AiProfileRepository aiProfileRepository) {

        this.userRepository = userRepository;
        this.aiProfileRepository = aiProfileRepository;
    }

    @GetMapping("/")
    public String showWelcomePage() {
        return "index";
    }

    @GetMapping("/quick-login")
    public String showQuickLoginPage() {
        return "quick-login";
    }

    @PostMapping("/quick-login")
    public String quickLogin(
            @RequestParam String name,
            @RequestParam int age,
            Model model,
            HttpSession session) {

        User user = new User(name, age);

        User savedUser = userRepository.save(user);

        // Remember the logged-in user
        session.setAttribute("userId", savedUser.getId());
        session.setAttribute("userName", savedUser.getName());

        model.addAttribute("name", name);

        model.addAttribute(
                "profiles",
                aiProfileRepository.findAll()
        );

        return "home";
    }
    
    // HOME ROUTE
    @GetMapping("/home")
    public String showHome(Model model) {

        model.addAttribute(
                "profiles",
                aiProfileRepository.findAll()
        );

        return "home";
    }
    
    @GetMapping("/api/user/coins")
    @ResponseBody
    public Map<String, Object> getUserCoins(HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return Map.of(
                    "loggedIn", false,
                    "coins", 0
            );
        }

        User user = userRepository.findById(userId)
                .orElse(null);

        if (user == null) {
            return Map.of(
                    "loggedIn", false,
                    "coins", 0
            );
        }

        return Map.of(
                "loggedIn", true,
                "coins", user.getCoins()
        );
    }
    
    @PostMapping("/api/test/add-coins")
    @ResponseBody
    public Map<String, Object> addTestCoins(
            @RequestParam int coins,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return Map.of(
                    "success", false,
                    "error", "User is not logged in"
            );
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return Map.of(
                    "success", false,
                    "error", "User not found"
            );
        }

        user.setCoins(user.getCoins() + coins);
        userRepository.save(user);

        return Map.of(
                "success", true,
                "coins", user.getCoins(),
                "message", "Test coins added"
        );
    }
    
    @GetMapping("/user-profile")
    public String showUserProfile(
            Model model,
            HttpSession session) {

        Long userId =
                (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/quick-login";
        }

        User user =
                userRepository.findById(userId)
                        .orElse(null);

        if (user == null) {
            return "redirect:/quick-login";
        }

        model.addAttribute("user", user);

        return "user-profile";
    }
     
    @PostMapping("/api/user/update-profile")
    @ResponseBody
    public Map<String, Object> updateProfile(
            @RequestParam String name,
            @RequestParam int age,
            HttpSession session) {

        Long userId =
                (Long) session.getAttribute("userId");

        if (userId == null) {
            return Map.of(
                    "success", false,
                    "error", "User is not logged in"
            );
        }

        User user =
                userRepository.findById(userId)
                        .orElse(null);

        if (user == null) {
            return Map.of(
                    "success", false,
                    "error", "User not found"
            );
        }

        if (name == null || name.trim().isEmpty()) {
            return Map.of(
                    "success", false,
                    "error", "Name cannot be empty"
            );
        }

        if (age < 18 || age > 100) {
            return Map.of(
                    "success", false,
                    "error", "Age must be between 18 and 100"
            );
        }

        user.setName(name.trim());
        user.setAge(age);

        userRepository.save(user);

        // Update session name too
        session.setAttribute("userName", user.getName());

        return Map.of(
                "success", true,
                "name", user.getName(),
                "age", user.getAge()
        );
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {

        // Destroy the current login session
        session.invalidate();

        // Go back to Deedi welcome page
        return "redirect:/";
    }
    
}
