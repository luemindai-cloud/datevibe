package com.aivideochat.repository;

import com.aivideochat.entity.VideoCallSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface VideoCallSessionRepository
        extends JpaRepository<VideoCallSession, Long> {
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
	       SELECT s
	       FROM VideoCallSession s
	       WHERE s.id = :id
	       AND s.userId = :userId
	       AND s.active = true
	       """)
	Optional<VideoCallSession> findActiveSessionForUpdate(
	        @Param("id") Long id,
	        @Param("userId") Long userId
	);

    Optional<VideoCallSession> findByIdAndUserIdAndActiveTrue(
            Long id,
            Long userId
    );
}