package com.aivideochat.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class PaymentService {

    @Value("${payment.upi.id:deedi@upi}")
    private String upiId;

    @Value("${payment.upi.name:DEEDI AI}")
    private String payeeName;

    @Value("${payment.upi.auto-approve:false}")
    private boolean autoApprove = false;

    public record PackageInfo(int amount, int coins) {}

    public PackageInfo getPackageInfo(String packageId) {
        if (packageId == null) return null;
        return switch (packageId.trim()) {
            case "100" -> new PackageInfo(100, 3000);
            case "199" -> new PackageInfo(199, 5000);
            case "399" -> new PackageInfo(399, 10000);
            case "1599" -> new PackageInfo(1599, 50000);
            default -> null;
        };
    }

    public String getUpiId() {
        return upiId;
    }

    public synchronized void setUpiId(String upiId) {
        if (upiId != null && !upiId.isBlank()) {
            this.upiId = upiId.trim();
        }
    }

    public String getPayeeName() {
        return payeeName;
    }

    public synchronized void setPayeeName(String payeeName) {
        if (payeeName != null && !payeeName.isBlank()) {
            this.payeeName = payeeName.trim();
        }
    }

    public boolean isAutoApprove() {
        return autoApprove;
    }

    public synchronized void setAutoApprove(boolean autoApprove) {
        this.autoApprove = autoApprove;
    }

    /**
     * Standard UPI Intent URI formatted per NPCI UPI specification.
     */
    public String buildUpiUri(int amount, String orderId) {
        return buildUriWithScheme("upi://pay", amount, orderId);
    }

    /**
     * Google Pay deep-link
     */
    public String buildGpayUri(int amount, String orderId) {
        return buildUriWithScheme("tez://upi/pay", amount, orderId);
    }

    /**
     * PhonePe deep-link
     */
    public String buildPhonepeUri(int amount, String orderId) {
        return buildUriWithScheme("phonepe://pay", amount, orderId);
    }

    /**
     * Paytm deep-link
     */
    public String buildPaytmUri(int amount, String orderId) {
        return buildUriWithScheme("paytmmp://pay", amount, orderId);
    }

    private String buildUriWithScheme(String scheme, int amount, String orderId) {
        String encodedName = URLEncoder.encode(payeeName, StandardCharsets.UTF_8);
        String encodedNote = URLEncoder.encode("Recharge " + orderId, StandardCharsets.UTF_8);
        return scheme + "?pa=" + upiId +
                "&pn=" + encodedName +
                "&am=" + amount + ".00" +
                "&cu=INR" +
                "&tn=" + encodedNote;
    }
}
