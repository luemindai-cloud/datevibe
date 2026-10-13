package com.aivideochat.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aivideochat.entity.PaymentOrder;
import com.aivideochat.entity.User;
import com.aivideochat.repository.PaymentOrderRepository;
import com.aivideochat.repository.UserRepository;
import com.aivideochat.service.CashfreeService;
import com.cashfree.pg.model.OrderEntity;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import com.cashfree.pg.model.PaymentEntity;

import java.math.BigDecimal;

@RestController
public class PaymentController {

    private final CashfreeService cashfreeService;
    private final UserRepository userRepository;
    private final PaymentOrderRepository paymentOrderRepository;

    public PaymentController(
            CashfreeService cashfreeService,
            UserRepository userRepository,
            PaymentOrderRepository paymentOrderRepository) {

        this.cashfreeService = cashfreeService;
        this.userRepository = userRepository;
        this.paymentOrderRepository = paymentOrderRepository;
    }

    @PostMapping("/api/payment/create")
    public ResponseEntity<?> createPayment(
            @RequestParam String packageId,
            HttpSession session,
            HttpServletRequest request) {

        // Get logged-in user
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error",
                            "User is not logged in"
                    ));
        }

        // Make sure user actually exists
        User user = userRepository.findById(userId)
                .orElse(null);

        if (user == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error",
                            "User not found"
                    ));
        }

        // Package details decided by SERVER
        int amount;
        int coins;

        switch (packageId) {

            case "100":
                amount = 100;
                coins = 3000;
                break;

            case "199":
                amount = 199;
                coins = 5000;
                break;

            case "399":
                amount = 399;
                coins = 10000;
                break;

            case "1599":
                amount = 1599;
                coins = 50000;
                break;

            default:
                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "error",
                                "Invalid package"
                        ));
        }

        // Generate unique Cashfree order ID
        String orderId =
                "ORDER_" + UUID.randomUUID();

        try {

            // Save payment as PENDING
            PaymentOrder paymentOrder =
                    new PaymentOrder(
                            userId,
                            orderId,
                            amount,
                            coins
                    );

            paymentOrderRepository.save(paymentOrder);

            // Get the request base URL dynamically
            String baseUrl = getBaseUrl(request);
            
            // Create Cashfree order with dynamic return URL
            OrderEntity order =
                    cashfreeService.createOrder(
                            orderId,
                            amount,
                            userId,
                            baseUrl
                    );

            // Send payment session ID to frontend
            return ResponseEntity.ok(
                    Map.of(
                            "orderId", order.getOrderId(),
                            "paymentSessionId",
                                    order.getPaymentSessionId()
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(Map.of(
                            "error",
                            "Unable to create payment order"
                    ));
        }
    }
    
    /**
     * Extract base URL from request (handles proxies like Cloudflare)
     */
    private String getBaseUrl(HttpServletRequest request) {
        
        // Check for X-Forwarded-Proto header (set by proxies like Cloudflare)
        String scheme = request.getHeader("X-Forwarded-Proto");
        if (scheme == null || scheme.isEmpty()) {
            scheme = request.getScheme();
        }
        
        // Check for X-Forwarded-Host header (set by proxies)
        String host = request.getHeader("X-Forwarded-Host");
        if (host == null || host.isEmpty()) {
            host = request.getHeader("Host");
        }
        
        String baseUrl = scheme + "://" + host;
        
        System.out.println("========== DYNAMIC BASE URL ==========");
        System.out.println("Scheme: " + scheme);
        System.out.println("Host: " + host);
        System.out.println("Base URL: " + baseUrl);
        System.out.println("=====================================");
        
        return baseUrl;
    }
    
    @PostMapping("/api/payment/verify")
    public ResponseEntity<?> verifyPayment(
            @RequestParam String orderId,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "User is not logged in"));
        }

        try {

            // Find our payment order
            PaymentOrder paymentOrder =
                    paymentOrderRepository
                            .findByOrderId(orderId)
                            .orElse(null);

            if (paymentOrder == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Payment order not found"));
            }

            // Make sure this order belongs to logged-in user
            if (!paymentOrder.getUserId().equals(userId)) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Invalid payment order"));
            }

            // Don't credit coins twice
            if ("SUCCESS".equalsIgnoreCase(
                    paymentOrder.getStatus())) {

                return ResponseEntity.ok(
                        Map.of(
                                "success", true,
                                "message", "Payment already verified"
                        )
                );
            }

            // Ask Cashfree for all payments for this order
            List<PaymentEntity> payments =
                    cashfreeService.fetchPayments(orderId);

            System.out.println(
                    "Cashfree payments found: "
                    + payments.size()
            );

            PaymentEntity successfulPayment = null;

            for (PaymentEntity payment : payments) {


                System.out.println(
                        "Payment status: "
                        + payment.getPaymentStatus()
                );

                System.out.println(
                        "Payment amount: "
                        + payment.getPaymentAmount()
                );

                if ("SUCCESS".equalsIgnoreCase(
                        String.valueOf(
                                payment.getPaymentStatus()
                        ))) {

                    successfulPayment = payment;
                    break;
                }
            }

            // Payment has not succeeded yet
            if (successfulPayment == null) {

                return ResponseEntity.ok(
                        Map.of(
                                "success", false,
                                "message",
                                "Payment is not completed yet."
                        )
                );
            }

            // Verify the successful payment amount
            BigDecimal paidAmount =
                    successfulPayment.getPaymentAmount();

            BigDecimal expectedAmount =
                    BigDecimal.valueOf(
                            paymentOrder.getAmount()
                    );

            if (paidAmount == null ||
                    paidAmount.compareTo(expectedAmount) != 0) {

                System.out.println(
                        "⚠️ Payment amount mismatch!"
                );

                return ResponseEntity.badRequest().body(
                        Map.of(
                                "error",
                                "Payment amount mismatch"
                        )
                );
            }

            // Get user
            User user =
                    userRepository.findById(userId)
                            .orElse(null);

            if (user == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "User not found"));
            }

            // Add package coins
            user.setCoins(
                    user.getCoins()
                            + paymentOrder.getCoins()
            );

            userRepository.save(user);

            // Mark our order as successfully verified
            paymentOrder.setStatus("SUCCESS");

            paymentOrderRepository.save(paymentOrder);

            System.out.println(
                    "🎉 Payment verified successfully!"
            );

            System.out.println(
                    "🪙 Coins added: "
                    + paymentOrder.getCoins()
            );

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "coins", paymentOrder.getCoins(),
                            "message",
                            "Payment verified and coins added"
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "error",
                                    "Unable to verify payment"
                            )
                    );
        }
    }
    
    @GetMapping("/payment-return")
    public ResponseEntity<?> paymentReturn(
            @RequestParam("order_id") String orderId,
            HttpSession session) {

        System.out.println(
                "🔄 Cashfree returned with order ID: "
                + orderId
        );

        // Verify payment on our server
        ResponseEntity<?> verification =
                verifyPayment(orderId, session);

        System.out.println(
                "💳 Payment verification result: "
                + verification.getBody()
        );

        // Send user back to Home
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header("Location", "/home")
                .build();
    }
}
