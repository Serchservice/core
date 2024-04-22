package com.serch.server.repositories.referral;

import com.serch.server.models.referral.Referral;
import com.serch.server.models.referral.ReferralProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReferralRepository extends JpaRepository<Referral, String> {
    List<Referral> findByReferredBy_User_EmailAddress(@NonNull String emailAddress);
    List<Referral> findByReferral_Id(@NonNull UUID id);
    @Query("SELECT COUNT(r) FROM Referral r WHERE r.referredBy = :referredBy AND r.referral IN (SELECT r2.referral FROM Referral r2 WHERE r2.referredBy = :referredBy)")
    int countReferralsOfReferralsByReferredBy(@Param("referredBy") ReferralProgram referredBy);
    @Query("SELECT COUNT(r) FROM Referral r WHERE r.referredBy = :referredBy AND r.referral IN (SELECT r2.referral FROM Referral r2 WHERE r2.referredBy = :referredBy) AND r.createdAt BETWEEN :startDate AND :endDate GROUP BY r.referredBy HAVING COUNT(r) BETWEEN 5 AND 10")
    int countTimeLimitedReferralsOfReferralsByReferredBy(@Param("referredBy") ReferralProgram referredBy, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    @Query("SELECT COUNT(r) FROM Referral r WHERE r.referredBy = :referredBy AND r.createdAt BETWEEN :startDate AND :endDate GROUP BY r.referredBy HAVING COUNT(r) BETWEEN 5 AND 10")
    int countTimeLimitedReferralsByReferredBy(@Param("referredBy") ReferralProgram referredBy, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}