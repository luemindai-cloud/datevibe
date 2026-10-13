package com.aivideochat.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cashfree.pg.ApiException;
import com.cashfree.pg.ApiResponse;
import com.cashfree.pg.Cashfree;
import com.cashfree.pg.model.CreateOrderRequest;
import com.cashfree.pg.model.CustomerDetails;
import com.cashfree.pg.model.OrderEntity;
import com.cashfree.pg.model.OrderMeta;
import com.cashfree.pg.model.PaymentEntity;

@Service
public class CashfreeService {

    private final Cashfree cashfree;

    public CashfreeService(
            @Value("${cashfree.app-id}") String appId,
            @Value("${cashfree.secret-key}") String secretKey) {

        this.cashfree = new Cashfree(
                Cashfree.SANDBOX,
                appId,
                secretKey,
                null,
                null,
                null
        );
    }

    public OrderEntity createOrder(
            String orderId,
            double amount,
            Long userId,
            String baseUrl) throws ApiException {

        CustomerDetails customerDetails =
                new CustomerDetails();

        customerDetails.setCustomerId(
                "user_" + userId
        );

        customerDetails.setCustomerPhone(
                "9999999999"
        );

        CreateOrderRequest request =
                new CreateOrderRequest();

        request.setOrderId(orderId);

        request.setOrderAmount(
                BigDecimal.valueOf(amount)
        );

        request.setOrderCurrency("INR");

        request.setCustomerDetails(
                customerDetails
        );

        // Cashfree return URL - use dynamically detected base URL
        OrderMeta orderMeta = new OrderMeta();

        String returnUrl = baseUrl + "/payment-return?order_id={order_id}";
        
        System.out.println("💳 Cashfree Return URL: " + returnUrl);
        
        orderMeta.setReturnUrl(returnUrl);

        request.setOrderMeta(orderMeta);

        ApiResponse<OrderEntity> response =
                cashfree.PGCreateOrder(
                        request,
                        null,
                        null,
                        null
                );

        return response.getData();
    }

    public OrderEntity fetchOrder(String orderId)
            throws ApiException {

        ApiResponse<OrderEntity> response =
                cashfree.PGFetchOrder(
                        orderId,
                        null,
                        null,
                        null
                );

        return response.getData();
    }

    public List<PaymentEntity> fetchPayments(String orderId)
            throws ApiException {

        ApiResponse<List<PaymentEntity>> response =
                cashfree.PGOrderFetchPayments(
                        orderId,
                        null,
                        null,
                        null
                );

        return response.getData();
    }
}
