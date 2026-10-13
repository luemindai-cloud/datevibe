package com.aivideochat.repository;

import com.aivideochat.entity.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentOrderRepository
        extends JpaRepository<PaymentOrder, Long> {

    Optional<PaymentOrder> findByOrderId(String orderId);

    List<PaymentOrder> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<PaymentOrder> findByStatusOrderByCreatedAtDesc(String status);

}