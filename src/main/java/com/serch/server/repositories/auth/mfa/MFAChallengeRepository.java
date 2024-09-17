package com.serch.server.repositories.auth.mfa;

import com.serch.server.models.auth.mfa.MFAChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.UUID;

public interface MFAChallengeRepository extends JpaRepository<MFAChallenge, UUID> {
    @Query("SELECT COUNT(c) FROM MFAChallenge c WHERE c.mfaFactor.user.id = ?1 AND c.createdAt BETWEEN ?2 AND ?3")
    long countByUserAndDateRange(UUID userId, ZonedDateTime startDate, ZonedDateTime endDate);
}