package com.aivideochat.controller;

import com.aivideochat.entity.Admin;
import com.aivideochat.entity.User;
import com.aivideochat.entity.PaymentOrder;
import com.aivideochat.repository.AdminRepository;
import com.aivideochat.repository.UserRepository;
import com.aivideochat.repository.PaymentOrderRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PaymentOrderRepository paymentOrderRepository;

    public AdminController(
            AdminRepository adminRepository,
            UserRepository userRepository,
            PaymentOrderRepository paymentOrderRepository) {

        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.paymentOrderRepository = paymentOrderRepository;
    }

    // =========================
    // ADMIN LOGIN PAGE
    // =========================

    @GetMapping("/login")
    public String showAdminLogin() {
        return "admin/login";
    }

    // =========================
    // ADMIN LOGIN
    // =========================

    @PostMapping("/login")
    public String adminLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        Admin admin = adminRepository
                .findByUsername(username)
                .orElse(null);

        if (admin == null ||
            !admin.getPassword().equals(password)) {

            model.addAttribute(
                    "error",
                    "Invalid username or password"
            );

            return "admin/login";
        }

        session.setAttribute(
                "adminId",
                admin.getId()
        );

        session.setAttribute(
                "adminUsername",
                admin.getUsername()
        );

        session.setAttribute(
                "adminLoggedIn",
                true
        );

        return "redirect:/admin/dashboard";
    }

    // =========================
    // ADMIN DASHBOARD
    // =========================

    @GetMapping("/dashboard")
    public String adminDashboard(
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }

        model.addAttribute(
                "adminUsername",
                session.getAttribute("adminUsername")
        );

        return "admin/dashboard";
    }

 // =========================
 // ADMIN USERS
 // =========================

 @GetMapping("/users")
 public String adminUsers(
         HttpSession session,
         Model model) {

     if (!isAdmin(session)) {
         return "redirect:/admin/login";
     }

     List<User> users = userRepository.findAll();

     // Stores total successful payment amount for each user
     Map<Long, Integer> totalPaidMap = new HashMap<>();

     // Stores whether each user has successfully paid
     Map<Long, Boolean> paidStatusMap = new HashMap<>();

     for (User user : users) {

         List<PaymentOrder> payments =
                 paymentOrderRepository
                         .findByUserIdOrderByCreatedAtDesc(user.getId());

         int totalPaid = 0;

         for (PaymentOrder payment : payments) {

             if ("SUCCESS".equalsIgnoreCase(payment.getStatus())) {
                 totalPaid += payment.getAmount();
             }
         }

         totalPaidMap.put(user.getId(), totalPaid);
         paidStatusMap.put(user.getId(), totalPaid > 0);
     }

     model.addAttribute("users", users);
     model.addAttribute("totalPaidMap", totalPaidMap);
     model.addAttribute("paidStatusMap", paidStatusMap);

     return "admin/users";
 }

    // =========================
    // USER PAYMENT DETAILS
    // =========================

    @GetMapping("/users/{id}")
    public String userDetails(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }

        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return "redirect:/admin/users";
        }

        List<PaymentOrder> payments =
                paymentOrderRepository
                        .findByUserIdOrderByCreatedAtDesc(id);

        int totalPaid = 0;

        for (PaymentOrder payment : payments) {

            if ("SUCCESS".equalsIgnoreCase(payment.getStatus())) {
                totalPaid += payment.getAmount();
            }
        }

        boolean hasPaid = totalPaid > 0;

        model.addAttribute("user", user);
        model.addAttribute("payments", payments);
        model.addAttribute("totalPaid", totalPaid);
        model.addAttribute("hasPaid", hasPaid);

        return "admin/user-details";
    }
    
 // =========================
 // ALL PAYMENTS
 // =========================

    @GetMapping("/payments")
    public String adminPayments(
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }

        List<PaymentOrder> payments =
                paymentOrderRepository.findAll(
                        org.springframework.data.domain.Sort.by(
                                org.springframework.data.domain.Sort.Direction.DESC,
                                "createdAt"
                        )
                );

        List<User> users = userRepository.findAll();

        Map<Long, User> userMap = new HashMap<>();

        for (User user : users) {
            userMap.put(user.getId(), user);
        }

        // 💰 Calculate total successful money received
        int totalReceived = 0;

        for (PaymentOrder payment : payments) {

            if ("SUCCESS".equalsIgnoreCase(payment.getStatus())) {
                totalReceived += payment.getAmount();
            }
        }

        model.addAttribute("payments", payments);
        model.addAttribute("userMap", userMap);
        model.addAttribute("totalReceived", totalReceived);

        return "admin/payments";
    }

    // =========================
    // ADMIN LOGOUT
    // =========================

    @GetMapping("/logout")
    public String adminLogout(
            HttpSession session) {

        session.removeAttribute("adminId");
        session.removeAttribute("adminUsername");
        session.removeAttribute("adminLoggedIn");

        return "redirect:/admin/login";
    }

    // =========================
    // ADMIN SECURITY CHECK
    // =========================

    private boolean isAdmin(HttpSession session) {

        Boolean adminLoggedIn =
                (Boolean) session.getAttribute("adminLoggedIn");

        return adminLoggedIn != null && adminLoggedIn;
    }
}