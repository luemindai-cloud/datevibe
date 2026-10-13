package com.aivideochat.repository;

import com.aivideochat.entity.GiftTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GiftTransactionRepository
        extends JpaRepository<GiftTransaction, Long> {

    List<GiftTransaction> findByUserId(Long userId);

    List<GiftTransaction> findByProfileId(Long profileId);
}