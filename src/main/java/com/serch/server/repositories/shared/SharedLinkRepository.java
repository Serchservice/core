package com.serch.server.repositories.shared;

import com.serch.server.models.referral.ReferralProgram;
import com.serch.server.models.shared.SharedLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SharedLinkRepository extends JpaRepository<SharedLink, String> {
    Optional<SharedLink> findByLink(@NonNull String link);
    @Query("select s from SharedLink s where s.user.id = ?1 or s.provider.id = ?1 or s.provider.business.id = ?1")
    List<SharedLink> findByUserId(@NonNull UUID id);
    @Query("SELECT COUNT(sl) FROM SharedLink sl WHERE sl.user.user IN (SELECT r2.referral FROM Referral r2 WHERE r2.referredBy = :referredBy) OR sl.provider.user IN (SELECT r2.referral FROM Referral r2 WHERE r2.referredBy = :referredBy)")
    int countSharedLinksOfReferralsOfReferralsByReferredBy(@Param("referredBy") ReferralProgram referredBy);
    @Query("SELECT COUNT(sl) FROM SharedLink sl WHERE sl.user.id = :referredBy OR sl.provider.id = :referredBy OR sl.provider.business.id = :referredBy")
    int countSharedLinksForUser(@Param("referredBy") UUID referredBy);
    @Query("SELECT sl FROM SharedLink sl " +
            "JOIN sl.logins login " +
            "WHERE login.status = 'USED'")
    List<SharedLink> findSharedLinksWithUsedStatus();
}