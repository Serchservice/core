package com.serch.server.repositories.trip;

import com.serch.server.models.trip.Active;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActiveRepository extends JpaRepository<Active, Long> {
    Optional<Active> findByProfile_Id(@NonNull UUID id);

    @Query(
            value = "SELECT ap.* FROM platform.active_providers ap " +
                    "JOIN account.profiles p ON ap.serch_id = p.serch_id " +
                    "LEFT JOIN account.business_profiles busi ON p.business_id = busi.serch_id " +
                    "LEFT JOIN verified.verification verify ON ap.serch_id = verify.serch_id " +
                    "LEFT JOIN verified.verification verifyBusi ON busi.serch_id = verify.serch_id " +
                    "JOIN account.specializations s ON p.serch_id = s.serch_id " +
                    "LEFT JOIN subscription.subscriptions sub ON p.serch_id = sub.serch_id " +
                    "LEFT JOIN subscription.subscriptions subBusi ON busi.serch_id = sub.serch_id " +
                    "LEFT JOIN company.plan_parents parent ON sub.plan = parent.id " +
                    "LEFT JOIN company.plan_children child ON sub.sub_plan = child.id " +
                    "LEFT JOIN company.plan_parents parentBusi ON subBusi.plan = parentBusi.id " +
                    "LEFT JOIN company.plan_children childBusi ON subBusi.sub_plan = childBusi.id " +
                    "WHERE " +
                    "SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325 < :radius " +
                    "AND p.serch_category = :category " +
                    "GROUP BY ap.id, parent.type, child.type, parentBusi.type, childBusi.type, " +
                    "s.specialty, p.serch_category, verify.status, verifyBusi.status, " +
                    "ap.latitude, ap.longitude, ap.status, p.rating " +
                    "ORDER BY " +
                    "SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325, " +
                    "CASE " +
                    "WHEN parent.type = 'PREMIUM' AND parentBusi.type = 'PREMIUM' THEN 0 " +
                    "WHEN parent.type = 'ALL_DAY' AND parentBusi.type = 'ALL_DAY' THEN 1 " +
                    "ELSE 2 END, " +
                    "CASE " +
                    "WHEN parent.type = 'PREMIUM' AND parentBusi.type = 'PREMIUM' THEN " +
                    "CASE COALESCE(child.type, childBusi.type) " +
                    "WHEN 'QUARTERLY' THEN 0 " +
                    "WHEN 'MONTHLY' THEN 1 " +
                    "WHEN 'WEEKLY' THEN 2 " +
                    "WHEN 'DAILY' THEN 3 " +
                    "ELSE 4 END " +
                    "WHEN parent.type = 'ALL_DAY' AND parentBusi.type = 'ALL_DAY' THEN " +
                    "CASE COALESCE(child.type, childBusi.type) " +
                    "WHEN 'QUARTERLY' THEN 5 " +
                    "WHEN 'MONTHLY' THEN 6 " +
                    "WHEN 'WEEKLY' THEN 7 " +
                    "WHEN 'DAILY' THEN 8 " +
                    "ELSE 9 END " +
                    "ELSE 10 END, " +
                    "CASE " +
                    "WHEN verify.status = 'VERIFIED' AND verifyBusi.status = 'VERIFIED' THEN 0 " +
                    "WHEN verify.status = 'REQUESTED' AND verifyBusi.status = 'REQUESTED' THEN 1 " +
                    "WHEN verify.status = 'ERROR' AND verifyBusi.status = 'ERROR' THEN 2 " +
                    "ELSE 3 END, " +
                    "CASE ap.status " +
                    "WHEN 'ONLINE' THEN 0 " +
                    "WHEN 'REQUESTSHARING' THEN 1 " +
                    "WHEN 'BUSY' THEN 2 " +
                    "WHEN 'OFFLINE' THEN 3 " +
                    "ELSE 4 END, " +
                    "p.rating DESC",
            nativeQuery = true
    )
    List<Active> sortAllWithinDistance(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radius") Double radius,
            @Param("category") String category
    );

    @Query(
            value = "SELECT ap.* FROM platform.active_providers ap " +
                    "JOIN account.profiles p ON ap.serch_id = p.serch_id " +
                    "LEFT JOIN account.business_profiles busi ON p.business_id = busi.serch_id " +
                    "LEFT JOIN verified.verification verify ON ap.serch_id = verify.serch_id " +
                    "LEFT JOIN verified.verification verifyBusi ON busi.serch_id = verify.serch_id " +
                    "JOIN account.specializations s ON p.serch_id = s.serch_id " +
                    "LEFT JOIN subscription.subscriptions sub ON p.serch_id = sub.serch_id " +
                    "LEFT JOIN subscription.subscriptions subBusi ON busi.serch_id = sub.serch_id " +
                    "LEFT JOIN company.plan_parents parent ON sub.plan = parent.id " +
                    "LEFT JOIN company.plan_children child ON sub.sub_plan = child.id " +
                    "LEFT JOIN company.plan_parents parentBusi ON subBusi.plan = parentBusi.id " +
                    "LEFT JOIN company.plan_children childBusi ON subBusi.sub_plan = childBusi.id " +
                    "WHERE " +
                    "SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325 < :radius " +
                    "AND (to_tsvector('english', s.specialty) @@ plainto_tsquery(:query) " +
                    "OR to_tsvector('english', p.serch_category) @@ plainto_tsquery(:query)) " +
                    "GROUP BY ap.id, parent.type, child.type, parentBusi.type, childBusi.type, " +
                    "s.specialty, p.serch_category, verify.status, verifyBusi.status, " +
                    "ap.latitude, ap.longitude, ap.status, p.rating " +
                    "ORDER BY " +
                    "SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325, " +
                    "CASE " +
                    "WHEN parent.type = 'PREMIUM' AND parentBusi.type = 'PREMIUM' THEN 0 " +
                    "WHEN parent.type = 'ALL_DAY' AND parentBusi.type = 'ALL_DAY' THEN 1 " +
                    "ELSE 2 END, " +
                    "CASE " +
                    "WHEN parent.type = 'PREMIUM' AND parentBusi.type = 'PREMIUM' THEN " +
                    "CASE COALESCE(child.type, childBusi.type) " +
                    "WHEN 'QUARTERLY' THEN 0 " +
                    "WHEN 'MONTHLY' THEN 1 " +
                    "WHEN 'WEEKLY' THEN 2 " +
                    "WHEN 'DAILY' THEN 3 " +
                    "ELSE 4 END " +
                    "WHEN parent.type = 'ALL_DAY' AND parentBusi.type = 'ALL_DAY' THEN " +
                    "CASE COALESCE(child.type, childBusi.type) " +
                    "WHEN 'QUARTERLY' THEN 5 " +
                    "WHEN 'MONTHLY' THEN 6 " +
                    "WHEN 'WEEKLY' THEN 7 " +
                    "WHEN 'DAILY' THEN 8 " +
                    "ELSE 9 END " +
                    "ELSE 10 END, " +
                    "CASE " +
                    "WHEN verify.status = 'VERIFIED' AND verifyBusi.status = 'VERIFIED' THEN 0 " +
                    "WHEN verify.status = 'REQUESTED' AND verifyBusi.status = 'REQUESTED' THEN 1 " +
                    "WHEN verify.status = 'ERROR' AND verifyBusi.status = 'ERROR' THEN 2 " +
                    "ELSE 3 END, " +
                    "CASE ap.status " +
                    "WHEN 'ONLINE' THEN 0 " +
                    "WHEN 'REQUESTSHARING' THEN 1 " +
                    "WHEN 'BUSY' THEN 2 " +
                    "WHEN 'OFFLINE' THEN 3 " +
                    "ELSE 4 END, " +
                    "GREATEST(ts_rank_cd(to_tsvector('english', s.specialty), plainto_tsquery(:query)), " +
                    "ts_rank_cd(to_tsvector('english', p.serch_category), plainto_tsquery(:query))) DESC, " +
                    "p.rating DESC",
            nativeQuery = true
    )
    List<Active> fullTextSearchWithinDistance(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("query") String query,
            @Param("radius") Double radius
    );

    @Query(
            value = "SELECT ap.* FROM platform.active_providers ap " +
                    "JOIN account.profiles p ON ap.serch_id = p.serch_id " +
                    "LEFT JOIN account.business_profiles busi ON p.business_id = busi.serch_id " +
                    "LEFT JOIN verified.verification verify ON ap.serch_id = verify.serch_id " +
                    "LEFT JOIN verified.verification verifyBusi ON busi.serch_id = verify.serch_id " +
                    "JOIN account.specializations s ON p.serch_id = s.serch_id " +
                    "LEFT JOIN subscription.subscriptions sub ON p.serch_id = sub.serch_id " +
                    "LEFT JOIN subscription.subscriptions subBusi ON busi.serch_id = sub.serch_id " +
                    "LEFT JOIN company.plan_parents parent ON sub.plan = parent.id " +
                    "LEFT JOIN company.plan_children child ON sub.sub_plan = child.id " +
                    "LEFT JOIN company.plan_parents parentBusi ON subBusi.plan = parentBusi.id " +
                    "LEFT JOIN company.plan_children childBusi ON subBusi.sub_plan = childBusi.id " +
                    "WHERE " +
                    "SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325 < :radius " +
                    "AND (to_tsvector('english', s.specialty) @@ plainto_tsquery(:query) " +
                    "OR to_tsvector('english', p.serch_category) @@ plainto_tsquery(:query)) " +
                    "GROUP BY ap.id, parent.type, child.type, parentBusi.type, childBusi.type," +
                    " s.specialty, p.serch_category," +
                    " verify.status, verifyBusi.status, ap.latitude, ap.longitude, ap.status, p.rating " +
                    "ORDER BY " +
                    "SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325, " +
                    "CASE " +
                    "WHEN parent.type = 'PREMIUM' AND parentBusi.type = 'PREMIUM' THEN 0 " +
                    "WHEN parent.type = 'ALL_DAY' AND parentBusi.type = 'ALL_DAY' THEN 1 " +
                    "ELSE 2 END, " +
                    "CASE " +
                    "WHEN parent.type = 'PREMIUM' AND parentBusi.type = 'PREMIUM' THEN " +
                    "CASE COALESCE(child.type, childBusi.type) " +
                    "WHEN 'QUARTERLY' THEN 0 " +
                    "WHEN 'MONTHLY' THEN 1 " +
                    "WHEN 'WEEKLY' THEN 2 " +
                    "WHEN 'DAILY' THEN 3 " +
                    "ELSE 4 END " +
                    "WHEN parent.type = 'ALL_DAY' AND parentBusi.type = 'ALL_DAY' THEN " +
                    "CASE COALESCE(child.type, childBusi.type) " +
                    "WHEN 'QUARTERLY' THEN 5 " +
                    "WHEN 'MONTHLY' THEN 6 " +
                    "WHEN 'WEEKLY' THEN 7 " +
                    "WHEN 'DAILY' THEN 8 " +
                    "ELSE 9 END " +
                    "ELSE 10 END, " +
                    "CASE " +
                    "WHEN verify.status = 'VERIFIED' AND verifyBusi.status = 'VERIFIED' THEN 0 " +
                    "WHEN verify.status = 'REQUESTED' AND verifyBusi.status = 'REQUESTED' THEN 1 " +
                    "WHEN verify.status = 'ERROR' AND verifyBusi.status = 'ERROR' THEN 2 " +
                    "ELSE 3 END, " +
                    "CASE ap.status " +
                    "WHEN 'ONLINE' THEN 0 " +
                    "WHEN 'REQUESTSHARING' THEN 1 " +
                    "WHEN 'BUSY' THEN 2 " +
                    "WHEN 'OFFLINE' THEN 3 " +
                    "ELSE 4 END, " +
                    "GREATEST(ts_rank_cd(to_tsvector('english', s.specialty), plainto_tsquery(:query)), " +
                    "ts_rank_cd(to_tsvector('english', p.serch_category), plainto_tsquery(:query))) DESC, " +
                    "p.rating DESC LIMIT 1",
            nativeQuery = true
    )
    Active findBestMatchWithQuery(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("query") String query,
            @Param("radius") Double radius
    );

    @Query(
            value = "SELECT ap.* FROM platform.active_providers ap " +
                    "JOIN account.profiles p ON ap.serch_id = p.serch_id " +
                    "LEFT JOIN account.business_profiles busi ON p.business_id = busi.serch_id " +
                    "LEFT JOIN verified.verification verify ON ap.serch_id = verify.serch_id " +
                    "LEFT JOIN verified.verification verifyBusi ON busi.serch_id = verify.serch_id " +
                    "LEFT JOIN account.specializations s ON p.serch_id = s.serch_id " +
                    "LEFT JOIN subscription.subscriptions sub ON p.serch_id = sub.serch_id " +
                    "LEFT JOIN subscription.subscriptions subBusi ON busi.serch_id = sub.serch_id " +
                    "LEFT JOIN company.plan_parents parent ON sub.plan = parent.id " +
                    "LEFT JOIN company.plan_children child ON sub.sub_plan = child.id " +
                    "LEFT JOIN company.plan_parents parentBusi ON subBusi.plan = parentBusi.id " +
                    "LEFT JOIN company.plan_children childBusi ON subBusi.sub_plan = childBusi.id " +
                    "WHERE " +
                    "SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325 < :radius " +
                    "AND p.serch_category = :category " +
                    "GROUP BY ap.id, parent.type, child.type, parentBusi.type, childBusi.type, " +
                    "s.specialty, p.serch_category, ap.status," +
                    " verify.status, verifyBusi.status, ap.latitude, ap.longitude, p.rating " +
                    "ORDER BY " +
                    "SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325, " +
                    "CASE " +
                    "WHEN parent.type = 'PREMIUM' AND parentBusi.type = 'PREMIUM' THEN 0 " +
                    "WHEN parent.type = 'ALL_DAY' AND parentBusi.type = 'ALL_DAY' THEN 1 " +
                    "ELSE 2 END, " +
                    "CASE " +
                    "WHEN parent.type = 'PREMIUM' AND parentBusi.type = 'PREMIUM' THEN " +
                    "CASE COALESCE(child.type, childBusi.type) " +
                    "WHEN 'QUARTERLY' THEN 0 " +
                    "WHEN 'MONTHLY' THEN 1 " +
                    "WHEN 'WEEKLY' THEN 2 " +
                    "WHEN 'DAILY' THEN 3 " +
                    "ELSE 4 END " +
                    "WHEN parent.type = 'ALL_DAY' AND parentBusi.type = 'ALL_DAY' THEN " +
                    "CASE COALESCE(child.type, childBusi.type) " +
                    "WHEN 'QUARTERLY' THEN 5 " +
                    "WHEN 'MONTHLY' THEN 6 " +
                    "WHEN 'WEEKLY' THEN 7 " +
                    "WHEN 'DAILY' THEN 8 " +
                    "ELSE 9 END " +
                    "ELSE 10 END, " +
                    "CASE " +
                    "WHEN verify.status = 'VERIFIED' AND verifyBusi.status = 'VERIFIED' THEN 0 " +
                    "WHEN verify.status = 'REQUESTED' AND verifyBusi.status = 'REQUESTED' THEN 1 " +
                    "WHEN verify.status = 'ERROR' AND verifyBusi.status = 'ERROR' THEN 2 " +
                    "ELSE 3 END, " +
                    "CASE ap.status " +
                    "WHEN 'ONLINE' THEN 0 " +
                    "WHEN 'REQUESTSHARING' THEN 1 " +
                    "WHEN 'BUSY' THEN 2 " +
                    "WHEN 'OFFLINE' THEN 3 " +
                    "ELSE 4 END, " +
                    "GREATEST(ts_rank_cd(to_tsvector('english', s.specialty), plainto_tsquery(:query)), " +
                    "ts_rank_cd(to_tsvector('english', p.serch_category), plainto_tsquery(:query))) DESC, " +
                    "p.rating DESC LIMIT 1",
            nativeQuery = true
    )
    Active findBestMatchWithCategory(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("category") String category,
            @Param("radius") Double radius
    );
}