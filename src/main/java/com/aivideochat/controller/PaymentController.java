package com.aivideochat.controller;

import com.aivideochat.entity.PaymentOrder;
import com.aivideochat.entity.User;
import com.aivideochat.repository.PaymentOrderRepository;
import com.aivideochat.repository.UserRepository;
import com.aivideochat.service.PaymentService;
import com.aivideochat.service.PaymentService.PackageInfo;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Controller
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;
    private final PaymentOrderRepository paymentOrderRepository;

    public PaymentController(
            PaymentService paymentService,
            UserRepository userRepository,
            PaymentOrderRepository paymentOrderRepository) {

        this.paymentService = paymentService;
        this.userRepository = userRepository;
        this.paymentOrderRepository = paymentOrderRepository;
    }

    // ========================================================
    // CREATE PAYMENT ORDER (API)
    // ========================================================
    @PostMapping("/api/payment/create")
    @ResponseBody
    public ResponseEntity<?> createPayment(
    		@RequestParam("packageId") String packageId,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "User is not logged in",
                    "redirectUrl", "/quick-login"
            ));
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "User not found"
            ));
        }

        PackageInfo pkg = paymentService.getPackageInfo(packageId);
        if (pkg == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Invalid package selection"
            ));
        }

        String orderId = "DEEDI_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();

        PaymentOrder paymentOrder = new PaymentOrder(
                userId,
                orderId,
                pkg.amount(),
                pkg.coins(),
                paymentService.getUpiId()
        );

        paymentOrderRepository.save(paymentOrder);

        String upiUri = paymentService.buildUpiUri(pkg.amount(), orderId);
        String checkoutUrl = "/payment/checkout?orderId=" + orderId;

        return ResponseEntity.ok(Map.of(
                "success", true,
                "orderId", orderId,
                "amount", pkg.amount(),
                "coins", pkg.coins(),
                "upiId", paymentService.getUpiId(),
                "payeeName", paymentService.getPayeeName(),
                "upiUri", upiUri,
                "checkoutUrl", checkoutUrl
        ));
    }

    // ========================================================
    // CHECKOUT WINDOW / PAGE
    // ========================================================
    @GetMapping("/payment/checkout")
    public String showCheckoutPage(
            @RequestParam("orderId") String orderId,
            HttpSession session,
            Model model) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/quick-login";
        }

        PaymentOrder order = paymentOrderRepository.findByOrderId(orderId).orElse(null);
        if (order == null) {
            return "redirect:/coins";
        }

        // If already completed, redirect directly to success page
        if ("SUCCESS".equalsIgnoreCase(order.getStatus())) {
            return "redirect:/payment/success?orderId=" + orderId;
        }

        User user = userRepository.findById(userId).orElse(null);

        String upiId = order.getUpiId() != null ? order.getUpiId() : paymentService.getUpiId();
        String payeeName = paymentService.getPayeeName();

        model.addAttribute("order", order);
        model.addAttribute("user", user);
        model.addAttribute("upiId", upiId);
        model.addAttribute("payeeName", payeeName);
        model.addAttribute("autoApprove", paymentService.isAutoApprove());
        model.addAttribute("upiUri", paymentService.buildUpiUri(order.getAmount(), order.getOrderId()));
        model.addAttribute("gpayUri", paymentService.buildGpayUri(order.getAmount(), order.getOrderId()));
        model.addAttribute("phonepeUri", paymentService.buildPhonepeUri(order.getAmount(), order.getOrderId()));
        model.addAttribute("paytmUri", paymentService.buildPaytmUri(order.getAmount(), order.getOrderId()));

        return "payment-checkout";
    }

    // ========================================================
    // SUBMIT UTR FOR VERIFICATION (API)
    // ========================================================
    @PostMapping("/api/payment/submit-utr")
    @ResponseBody
    public ResponseEntity<?> submitUtr(
    		@RequestParam("orderId") String orderId,
    		@RequestParam("utr") String utr,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "User is not logged in"
            ));
        }

        PaymentOrder order = paymentOrderRepository.findByOrderId(orderId).orElse(null);
        if (order == null || !order.getUserId().equals(userId)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Order not found"
            ));
        }

        // If already completed, redirect
        if ("SUCCESS".equalsIgnoreCase(order.getStatus())) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "status", "SUCCESS",
                    "redirectUrl", "/payment/success?orderId=" + orderId
            ));
        }

        String cleanUtr = (utr != null) ? utr.trim() : "";
        if (cleanUtr.length() < 6 || cleanUtr.length() > 25) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Please enter a valid UPI Reference / UTR Number (minimum 6 digits)."
            ));
        }

        // Prevent duplicate UTR abuse across orders
        if (paymentOrderRepository.existsByUtrNumber(cleanUtr) && !cleanUtr.equalsIgnoreCase(order.getUtrNumber())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "This UTR number has already been used for another payment. Please verify your reference number."
            ));
        }

        order.setUtrNumber(cleanUtr);

        // Check if admin enabled auto-approve or if manual admin approval is required
        if (paymentService.isAutoApprove()) {
            order.setStatus("SUCCESS");
            paymentOrderRepository.save(order);

            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                user.setCoins(user.getCoins() + order.getCoins());
                userRepository.save(user);
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "status", "SUCCESS",
                    "redirectUrl", "/payment/success?orderId=" + orderId
            ));
        } else {
            // Safe anti-cheat mode: saved as PENDING for admin review
            order.setStatus("PENDING");
            paymentOrderRepository.save(order);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "status", "PENDING",
                    "message", "UTR submitted! Your transaction is under verification. Coins will be credited once confirmed by admin."
            ));
        }
    }

    // ========================================================
    // PAYMENT SUCCESS PAGE
    // ========================================================
    @GetMapping("/payment/success")
    public String showSuccessPage(
            @RequestParam("orderId") String orderId,
            HttpSession session,
            Model model) {

        Long userId = (Long) session.getAttribute("userId");
        PaymentOrder order = paymentOrderRepository.findByOrderId(orderId).orElse(null);

        if (order == null) {
            return "redirect:/home";
        }

        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        } else if (order.getUserId() != null) {
            user = userRepository.findById(order.getUserId()).orElse(null);
        }

        model.addAttribute("order", order);
        model.addAttribute("user", user);

        return "payment-success";
    }

    // ========================================================
    // PAYMENT FAILED / CANCELLED PAGE
    // ========================================================
    @GetMapping("/payment/failed")
    public String showFailedPage(
            @RequestParam(value = "orderId", required = false) String orderId,
            Model model) {

        if (orderId != null) {
            PaymentOrder order = paymentOrderRepository.findByOrderId(orderId).orElse(null);
            model.addAttribute("order", order);
        }

        return "payment-failed";
    }

    // ========================================================
    // ORDER STATUS CHECK (API for live polling)
    // ========================================================
    @GetMapping("/api/payment/status")
    @ResponseBody
    public ResponseEntity<?> getPaymentStatus(@RequestParam("orderId") String orderId) {
        PaymentOrder order = paymentOrderRepository.findByOrderId(orderId).orElse(null);
        if (order == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Order not found"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "orderId", order.getOrderId(),
                "status", order.getStatus(),
                "amount", order.getAmount(),
                "coins", order.getCoins(),
                "utrNumber", order.getUtrNumber() != null ? order.getUtrNumber() : ""
        ));
    }
}
