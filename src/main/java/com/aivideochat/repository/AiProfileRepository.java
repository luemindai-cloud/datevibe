package com.aivideochat.repository;

import com.aivideochat.entity.AiProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiProfileRepository extends JpaRepository<AiProfile, Long> {
}