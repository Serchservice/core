package com.serch.server.repositories.trip;

import com.serch.server.models.trip.TripInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

public interface TripInviteRepository extends JpaRepository<TripInvite, String> {
    List<TripInvite> findByAccount(@NonNull String account);

    List<TripInvite> findByAccountAndLinkId(@NonNull String account, @NonNull String linkId);

    @Query(
            value = "SELECT DISTINCT ti.*, " +
                    "SQRT(POWER(ti.latitude - ap.latitude, 2) + POWER(ti.longitude - ap.longitude, 2)) * 111.325 AS distance, " +
                    "CASE " +
                    "WHEN verify.status = 'VERIFIED' AND verifyBusi.status = 'VERIFIED' THEN 0 " +
                    "WHEN verify.status = 'REQUESTED' AND verifyBusi.status = 'REQUESTED' THEN 1 " +
                    "WHEN verify.status = 'ERROR' AND verifyBusi.status = 'ERROR' THEN 2 " +
                    "ELSE 3 END AS verification_status, " +
                    "CASE ap.status " +
                    "WHEN 'ONLINE' THEN 0 " +
                    "WHEN 'REQUESTSHARING' THEN 1 " +
                    "WHEN 'BUSY' THEN 2 " +
                    "WHEN 'OFFLINE' THEN 3 " +
                    "ELSE 4 END AS provider_status, " +
                    "p.rating AS provider_rating " +
                    "FROM trip.invites ti " +
                    "LEFT JOIN platform.active_providers ap ON " +
                    "SQRT(POWER(ti.latitude - ap.latitude, 2) + POWER(ti.longitude - ap.longitude, 2)) * 111.325 < :radius " +
                    "LEFT JOIN account.profiles p ON ap.serch_id = p.serch_id " +
                    "LEFT JOIN account.business_profiles busi ON p.business_id = busi.serch_id " +
                    "LEFT JOIN identity.verification verify ON ap.serch_id = verify.serch_id " +
                    "LEFT JOIN identity.verification verifyBusi ON busi.serch_id = verifyBusi.serch_id " +
                    "LEFT JOIN account.specializations s ON p.serch_id = s.serch_id " +
                    "WHERE " +
                    "SQRT(POWER(ti.latitude - ap.latitude, 2) + POWER(ti.longitude - ap.longitude, 2)) * 111.325 < :radius " +
                    "AND ti.category = :category " +
                    "GROUP BY ti.id, ap.id, ti.latitude, ti.longitude, distance, verification_status, provider_status, provider_rating " +
                    "ORDER BY distance, verification_status, provider_status, provider_rating DESC",
            nativeQuery = true
    )
    List<TripInvite> sortAllWithinDistance(@Param("radius") Double radius, @Param("category") String category);

    List<TripInvite> findBySelected(@NonNull UUID selected);
}