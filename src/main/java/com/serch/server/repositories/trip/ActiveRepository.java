package com.serch.server.repositories.trip;

import com.serch.server.enums.auth.Role;
import com.serch.server.models.trip.Active;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActiveRepository extends JpaRepository<Active, Long> {
    Optional<Active> findByProfile_Id(@NonNull UUID id);

    @Query(
            value = """
                SELECT ap.* FROM platform.active_providers ap
                LEFT JOIN account.profiles p ON ap.serch_id = p.serch_id
                LEFT JOIN identity.users u ON ap.serch_id = u.id
                LEFT JOIN account.business_profiles busi ON p.business_id = busi.serch_id
                LEFT JOIN identity.verification verify ON ap.serch_id = verify.serch_id
                LEFT JOIN identity.verification verifyBusi ON busi.serch_id = verify.serch_id
                LEFT JOIN account.specializations s ON p.serch_id = s.serch_id
                WHERE
                SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325 < :radius / 1000
                AND p.serch_category = :category and u.status = 'ACTIVE'
                GROUP BY ap.id, s.specialty, p.serch_category, verify.status, verifyBusi.status, ap.latitude, ap.longitude, ap.status, p.rating
                ORDER BY SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325,
                CASE
                WHEN verify.status = 'VERIFIED' AND verifyBusi.status = 'VERIFIED' THEN 0
                WHEN verify.status = 'REQUESTED' AND verifyBusi.status = 'REQUESTED' THEN 1
                WHEN verify.status = 'ERROR' AND verifyBusi.status = 'ERROR' THEN 2
                ELSE 3 END,
                CASE ap.status
                WHEN 'ONLINE' THEN 0
                WHEN 'REQUESTSHARING' THEN 1
                WHEN 'BUSY' THEN 2
                WHEN 'OFFLINE' THEN 3
                ELSE 4 END,
                p.rating DESC
            """,
            nativeQuery = true
    )
    List<Active> sortAllWithinDistance(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radius") Double radius,
            @Param("category") String category
    );

    @Query(
            value = """
                SELECT ap.* FROM platform.active_providers ap
                LEFT JOIN account.profiles p ON ap.serch_id = p.serch_id
                LEFT JOIN identity.users u ON ap.serch_id = u.id
                LEFT JOIN account.business_profiles busi ON p.business_id = busi.serch_id
                LEFT JOIN identity.verification verify ON ap.serch_id = verify.serch_id
                LEFT JOIN identity.verification verifyBusi ON busi.serch_id = verify.serch_id
                LEFT JOIN account.specializations s ON p.serch_id = s.serch_id
                WHERE
                SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325 < :radius / 1000
                AND p.serch_category = :category and u.status = 'ACTIVE'
                GROUP BY ap.id, s.specialty, p.serch_category, verify.status, verifyBusi.status, ap.latitude, ap.longitude, ap.status, p.rating
                ORDER BY SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325,
                CASE
                WHEN verify.status = 'VERIFIED' AND verifyBusi.status = 'VERIFIED' THEN 0
                WHEN verify.status = 'REQUESTED' AND verifyBusi.status = 'REQUESTED' THEN 1
                WHEN verify.status = 'ERROR' AND verifyBusi.status = 'ERROR' THEN 2
                ELSE 3 END,
                CASE ap.status
                WHEN 'ONLINE' THEN 0
                WHEN 'REQUESTSHARING' THEN 1
                WHEN 'BUSY' THEN 2
                WHEN 'OFFLINE' THEN 3
                ELSE 4 END,
                p.rating DESC
            """,
            nativeQuery = true
    )
    Page<Active> sortAllWithinDistance(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radius") Double radius,
            @Param("category") String category,
            Pageable pageable
    );

    @Query(
            value = """
                SELECT ap.* FROM platform.active_providers ap
                LEFT JOIN account.profiles p ON ap.serch_id = p.serch_id
                LEFT JOIN identity.users u ON ap.serch_id = u.id
                LEFT JOIN account.business_profiles busi ON p.business_id = busi.serch_id
                LEFT JOIN identity.verification verify ON ap.serch_id = verify.serch_id
                LEFT JOIN identity.verification verifyBusi ON busi.serch_id = verify.serch_id
                LEFT JOIN account.specializations s ON p.serch_id = s.serch_id
                WHERE
                SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325 < :radius / 1000
                AND (to_tsvector('english', s.specialty) @@ to_tsquery(:query)
                OR to_tsvector('english', COALESCE(p.serch_category, '')) @@ to_tsquery(:query))
                and u.status = 'ACTIVE'
                GROUP BY ap.id, s.specialty, p.serch_category, verify.status, verifyBusi.status, ap.latitude, ap.longitude, ap.status, p.rating
                ORDER BY SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325,
                CASE
                WHEN verify.status = 'VERIFIED' AND verifyBusi.status = 'VERIFIED' THEN 0
                WHEN verify.status = 'REQUESTED' AND verifyBusi.status = 'REQUESTED' THEN 1
                WHEN verify.status = 'ERROR' AND verifyBusi.status = 'ERROR' THEN 2
                ELSE 3 END,
                CASE ap.status
                WHEN 'ONLINE' THEN 0
                WHEN 'REQUESTSHARING' THEN 1
                WHEN 'BUSY' THEN 2
                WHEN 'OFFLINE' THEN 3
                ELSE 4 END,
                GREATEST(ts_rank_cd(to_tsvector('english', s.specialty), to_tsquery(:query)),
                ts_rank_cd(to_tsvector('english', COALESCE(p.serch_category, '')), to_tsquery(:query))) DESC,
                p.rating DESC
            """,
            nativeQuery = true
    )
    Page<Active> fullTextSearchWithinDistance(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("query") String query,
            @Param("radius") Double radius,
            Pageable pageable
    );

    @Query(
            value = """
                SELECT ap.* FROM platform.active_providers ap
                LEFT JOIN account.profiles p ON ap.serch_id = p.serch_id
                LEFT JOIN identity.users u ON ap.serch_id = u.id
                LEFT JOIN account.business_profiles busi ON p.business_id = busi.serch_id
                LEFT JOIN identity.verification verify ON ap.serch_id = verify.serch_id
                LEFT JOIN identity.verification verifyBusi ON busi.serch_id = verify.serch_id
                LEFT JOIN account.specializations s ON p.serch_id = s.serch_id
                WHERE
                SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325 < :radius / 1000
                AND (to_tsvector('english', COALESCE(s.specialty, '')) @@ plainto_tsquery(:query)
                OR to_tsvector('english', COALESCE(p.serch_category, '')) @@ plainto_tsquery(:query))
                and u.status = 'ACTIVE'
                GROUP BY ap.id, s.specialty, p.serch_category, verify.status, verifyBusi.status, ap.latitude, ap.longitude, ap.status, p.rating
                ORDER BY SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325,
                CASE
                WHEN verify.status = 'VERIFIED' AND verifyBusi.status = 'VERIFIED' THEN 0
                WHEN verify.status = 'REQUESTED' AND verifyBusi.status = 'REQUESTED' THEN 1
                WHEN verify.status = 'ERROR' AND verifyBusi.status = 'ERROR' THEN 2
                ELSE 3 END,
                CASE ap.status
                WHEN 'ONLINE' THEN 0
                WHEN 'REQUESTSHARING' THEN 1
                WHEN 'BUSY' THEN 2
                WHEN 'OFFLINE' THEN 3
                ELSE 4 END,
                GREATEST(ts_rank_cd(to_tsvector('english', COALESCE(s.specialty, '')), plainto_tsquery(:query)),
                ts_rank_cd(to_tsvector('english', COALESCE(p.serch_category, '')), plainto_tsquery(:query))) DESC,
                p.rating DESC LIMIT 1
            """,
            nativeQuery = true
    )
    Active findBestMatchWithQuery(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("query") String query,
            @Param("radius") Double radius
    );

    @Query(
            value = """
                SELECT ap.* FROM platform.active_providers ap
                LEFT JOIN account.profiles p ON ap.serch_id = p.serch_id
                LEFT JOIN identity.users u ON ap.serch_id = u.id
                LEFT JOIN account.business_profiles busi ON p.business_id = busi.serch_id
                LEFT JOIN identity.verification verify ON ap.serch_id = verify.serch_id
                LEFT JOIN identity.verification verifyBusi ON busi.serch_id = verify.serch_id
                LEFT JOIN account.specializations s ON p.serch_id = s.serch_id
                WHERE
                SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325 < :radius / 1000
                AND p.serch_category = :category and u.status = 'ACTIVE'
                GROUP BY ap.id, s.specialty, p.serch_category, ap.status, verify.status, verifyBusi.status, ap.latitude, ap.longitude, p.rating
                ORDER BY SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325,
                CASE
                WHEN verify.status = 'VERIFIED' AND verifyBusi.status = 'VERIFIED' THEN 0
                WHEN verify.status = 'REQUESTED' AND verifyBusi.status = 'REQUESTED' THEN 1
                WHEN verify.status = 'ERROR' AND verifyBusi.status = 'ERROR' THEN 2
                ELSE 3 END,
                CASE ap.status
                WHEN 'ONLINE' THEN 0
                WHEN 'REQUESTSHARING' THEN 1
                WHEN 'BUSY' THEN 2
                WHEN 'OFFLINE' THEN 3
                ELSE 4 END,
                p.rating DESC LIMIT 1
            """,
            nativeQuery = true
    )
    Active findBestMatchWithCategory(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("category") String category,
            @Param("radius") Double radius
    );

    @Query(
            value = """
                SELECT ap.* FROM platform.active_providers ap
                LEFT JOIN account.profiles p ON ap.serch_id = p.serch_id
                LEFT JOIN identity.users u ON ap.serch_id = u.id
                LEFT JOIN account.business_profiles busi ON p.business_id = busi.serch_id
                LEFT JOIN identity.verification verify ON ap.serch_id = verify.serch_id
                LEFT JOIN identity.verification verifyBusi ON busi.serch_id = verify.serch_id
                LEFT JOIN account.specializations s ON p.serch_id = s.serch_id
                WHERE
                SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325 < :radius / 1000
                AND (
                  p.serch_category = :category OR (COALESCE(:filters, '') != '' AND (
                     to_tsvector('english', s.specialty) @@ to_tsquery(:filters) OR
                     to_tsvector('english', COALESCE(p.serch_category, '')) @@ to_tsquery(:filters)
                  ))
                )
                and u.status = 'ACTIVE'
                GROUP BY ap.id, s.specialty, p.serch_category, verify.status, verifyBusi.status, ap.latitude, ap.longitude, ap.status, p.rating
                ORDER BY SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325,
                CASE
                WHEN verify.status = 'VERIFIED' AND verifyBusi.status = 'VERIFIED' THEN 0
                WHEN verify.status = 'REQUESTED' AND verifyBusi.status = 'REQUESTED' THEN 1
                WHEN verify.status = 'ERROR' AND verifyBusi.status = 'ERROR' THEN 2
                ELSE 3 END,
                CASE ap.status
                WHEN 'ONLINE' THEN 0
                WHEN 'REQUESTSHARING' THEN 1
                WHEN 'BUSY' THEN 2
                WHEN 'OFFLINE' THEN 3
                ELSE 4 END,
                GREATEST(ts_rank_cd(to_tsvector('english', s.specialty), to_tsquery(:filters)),
                ts_rank_cd(to_tsvector('english', COALESCE(p.serch_category, '')), to_tsquery(:filters))) DESC,
                p.rating DESC
            """,
            nativeQuery = true
    )
    List<Active> searchWithinDistance(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radius") Double radius,
            @Param("category") String category,
            @Param("filters") String filters
    );

    @Query("select b from Active b where b.profile.user.role = ?1 and b.createdAt between ?2 and ?3")
    List<Active> findByRoleAndCreatedAtBetween(Role role, ZonedDateTime startMonth, ZonedDateTime endMonth);
}