package com.serch.server.repositories.trip;

import com.serch.server.models.trip.TripInvite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.UUID;

public interface TripInviteRepository extends JpaRepository<TripInvite, String> {
    Page<TripInvite> findByAccount(@NonNull String account, Pageable pageable);

    @Query(
            value = """
                SELECT DISTINCT ti.*,
                SQRT(POWER(ti.latitude - ap.latitude, 2) + POWER(ti.longitude - ap.longitude, 2)) * 111.325 / 1000 AS distance,
                CASE
                WHEN verify.status = 'VERIFIED' AND verifyBusi.status = 'VERIFIED' THEN 0
                WHEN verify.status = 'REQUESTED' AND verifyBusi.status = 'REQUESTED' THEN 1
                WHEN verify.status = 'ERROR' AND verifyBusi.status = 'ERROR' THEN 2
                ELSE 3 END AS verification_status,
                CASE ap.status
                WHEN 'ONLINE' THEN 0
                WHEN 'REQUESTSHARING' THEN 1
                WHEN 'BUSY' THEN 2
                WHEN 'OFFLINE' THEN 3
                ELSE 4 END AS provider_status,
                p.rating AS provider_rating
                FROM trip.invites ti
                LEFT JOIN platform.active_providers ap ON
                SQRT(POWER(ti.latitude - ap.latitude, 2) + POWER(ti.longitude - ap.longitude, 2)) * 111.325 < :radius / 1000
                LEFT JOIN account.profiles p ON ap.serch_id = p.serch_id
                LEFT JOIN account.business_profiles busi ON p.business_id = busi.serch_id
                LEFT JOIN identity.verification verify ON ap.serch_id = verify.serch_id
                LEFT JOIN identity.verification verifyBusi ON busi.serch_id = verifyBusi.serch_id
                LEFT JOIN account.specializations s ON p.serch_id = s.serch_id
                WHERE
                SQRT(POWER(ti.latitude - ap.latitude, 2) + POWER(ti.longitude - ap.longitude, 2)) * 111.325 < :radius / 1000
                AND ti.category = :category AND ti.selected is null
                GROUP BY ti.id, ap.id, ti.latitude, ti.longitude, distance, verification_status, provider_status, provider_rating
                ORDER BY distance, verification_status, provider_status, provider_rating DESC
            """,
            nativeQuery = true
    )
    Page<TripInvite> sortWithinDistanceWithNoneSelected(@Param("radius") Double radius, @Param("category") String category, Pageable pageable);

    Page<TripInvite> findBySelected(@NonNull UUID selected, Pageable pageable);

    @Query("select t from TripInvite t where t.account = ?1 and t.linkId = ?2 and t.selected is not null")
    Page<TripInvite> findByAccountAndLinkId(@NonNull String account, @NonNull String linkId, Pageable pageable);
}