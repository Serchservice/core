package com.serch.server.repositories.auth.mfa;

import com.serch.server.models.auth.mfa.MFAChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface MFAChallengeRepository extends JpaRepository<MFAChallenge, UUID> {
    @Query("SELECT COUNT(c) FROM MFAChallenge c WHERE c.mfaFactor.user.id = :userId AND c.createdAt BETWEEN :startDate AND :endDate")
    long countByUserAndDateRange(@Param("userId") UUID userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}