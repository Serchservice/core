package com.serch.server.repositories.trip;

import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.subscription.PlanStatus;
import com.serch.server.enums.trip.TripStatus;
import com.serch.server.enums.verified.VerificationStatus;
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
            value = "SELECT ap FROM Active ap " +
                    "JOIN ap.profile p on p.id = ap.profile.id " +
                    "JOIN Subscription planB on planB.user.id = p.business.id " +
                    "JOIN Subscription plan on plan.user.id = p.id " +
                    "WHERE " +
                    "SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325 < :radius " +
                    "AND p.category = :category " +
                    "AND plan.planStatus = :planStatus " +
                    "GROUP BY ap.id, planB.plan.type, planB.child.type, plan.plan.type, plan.child.type, " +
                    "p.verification.status, ap.latitude, ap.longitude, " +
                    "p.rating, ap.tripStatus " +
                    "ORDER BY " +
                    "SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325 ASC, " +
                    "CASE " +
                    "WHEN COALESCE(plan.plan.type, planB.plan.type) = 'PREMIUM' THEN 0 " +
                    "WHEN COALESCE(plan.plan.type, planB.plan.type) = 'ALL_DAY' THEN 1 " +
                    "ELSE 2 " +
                    "END ASC, " +
                    "CASE " +
                    "WHEN COALESCE(plan.plan.type, planB.plan.type) = 'PREMIUM' THEN " +
                    "CASE CAST(COALESCE(plan.child.type, planB.child.type) AS string) " +
                    "WHEN 'QUARTERLY' THEN 0 " +
                    "WHEN 'MONTHLY' THEN 1 " +
                    "WHEN 'WEEKLY' THEN 2 " +
                    "WHEN 'DAILY' THEN 3 " +
                    "ELSE 4 " +
                    "END " +
                    "WHEN COALESCE(plan.plan.type, planB.plan.type) = 'ALL_DAY' THEN " +
                    "CASE CAST(COALESCE(plan.child.type, planB.child.type) AS string) " +
                    "WHEN 'QUARTERLY' THEN 5 " +
                    "WHEN 'MONTHLY' THEN 6 " +
                    "WHEN 'WEEKLY' THEN 7 " +
                    "WHEN 'DAILY' THEN 8 " +
                    "ELSE 9 " +
                    "END " +
                    "ELSE 10 " +
                    "END ASC, " +
                    "CASE CAST(p.verification.status AS string) " +
                    "WHEN 'VERIFIED' THEN 0 " +
                    "WHEN 'REQUESTED' THEN 1 " +
                    "WHEN 'ERROR' THEN 2 " +
                    "ELSE 3 END ASC, " +
                    "CASE CAST(ap.tripStatus AS string) " +
                    "WHEN 'ONLINE' THEN 0 " +
                    "WHEN 'REQUESTSHARING' THEN 1 " +
                    "WHEN 'BUSY' THEN 2 " +
                    "WHEN 'OFFLINE' THEN 3 " +
                    "ELSE 4 END ASC, " +
                    "p.rating DESC"
    )
    List<Active> sortByRatingWithinDistance(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radius") Double radius,
            @Param("category") SerchCategory category,
            @Param("planStatus") PlanStatus planStatus
    );

    @Query(
            value = "SELECT ap FROM Active ap " +
                    "JOIN ap.profile p on p.id = ap.profile.id " +
                    "JOIN Subscription planB on planB.user.id = p.business.id " +
                    "JOIN Subscription plan on plan.user.id = p.id " +
                    "WHERE " +
                    "SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325 < :radius " +
                    "AND p.category = :category " +
                    "AND plan.planStatus = :planStatus " +
                    "AND p.verification.status = :status " +
                    "GROUP BY ap.id, planB.plan.type, planB.child.type, plan.plan.type, plan.child.type, " +
                    "p.verification.status, ap.latitude, ap.longitude, ap.tripStatus " +
                    "ORDER BY " +
                    "SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325 ASC, " +
                    "CASE " +
                    "WHEN COALESCE(plan.plan.type, planB.plan.type) = 'PREMIUM' THEN 0 " +
                    "WHEN COALESCE(plan.plan.type, planB.plan.type) = 'ALL_DAY' THEN 1 " +
                    "ELSE 2 " +
                    "END ASC, " +
                    "CASE " +
                    "WHEN COALESCE(plan.plan.type, planB.plan.type) = 'PREMIUM' THEN " +
                    "CASE CAST(COALESCE(plan.child.type, planB.child.type) AS string) " +
                    "WHEN 'QUARTERLY' THEN 0 " +
                    "WHEN 'MONTHLY' THEN 1 " +
                    "WHEN 'WEEKLY' THEN 2 " +
                    "WHEN 'DAILY' THEN 3 " +
                    "ELSE 4 " +
                    "END " +
                    "WHEN COALESCE(plan.plan.type, planB.plan.type) = 'ALL_DAY' THEN " +
                    "CASE CAST(COALESCE(plan.child.type, planB.child.type) AS string) " +
                    "WHEN 'QUARTERLY' THEN 5 " +
                    "WHEN 'MONTHLY' THEN 6 " +
                    "WHEN 'WEEKLY' THEN 7 " +
                    "WHEN 'DAILY' THEN 8 " +
                    "ELSE 9 " +
                    "END " +
                    "ELSE 10 " +
                    "END ASC, " +
                    "CASE CAST(p.verification.status AS string) " +
                    "WHEN 'VERIFIED' THEN 0 " +
                    "WHEN 'REQUESTED' THEN 1 " +
                    "WHEN 'ERROR' THEN 2 " +
                    "ELSE 3 END ASC, " +
                    "CASE CAST(ap.tripStatus AS string) " +
                    "WHEN 'ONLINE' THEN 0 " +
                    "WHEN 'REQUESTSHARING' THEN 1 " +
                    "WHEN 'BUSY' THEN 2 " +
                    "WHEN 'OFFLINE' THEN 3 " +
                    "ELSE 4 END ASC, " +
                    "p.verification.status DESC"
    )
    List<Active> sortByVerificationWithinDistance(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radius") Double radius,
            @Param("category") SerchCategory category,
            @Param("status") VerificationStatus status,
            @Param("planStatus") PlanStatus planStatus
    );

    @Query(
            value = "SELECT ap FROM Active ap " +
                    "JOIN ap.profile p on p.id = ap.profile.id " +
                    "JOIN Subscription planB on planB.user.id = p.business.id " +
                    "JOIN Subscription plan on plan.user.id = p.id " +
                    "WHERE " +
                    "SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325 < :radius " +
                    "AND p.category = :category " +
                    "AND ap.tripStatus = :tripStatus " +
                    "AND plan.planStatus = :planStatus " +
                    "GROUP BY ap.id, planB.plan.type, planB.child.type, plan.plan.type, plan.child.type, " +
                    "p.verification.status, ap.latitude, ap.longitude, " +
                    "p.rating, ap.tripStatus " +
                    "ORDER BY " +
                    "SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325 ASC, " +
                    "CASE " +
                    "WHEN COALESCE(plan.plan.type, planB.plan.type) = 'PREMIUM' THEN 0 " +
                    "WHEN COALESCE(plan.plan.type, planB.plan.type) = 'ALL_DAY' THEN 1 " +
                    "ELSE 2 " +
                    "END ASC, " +
                    "CASE " +
                    "WHEN COALESCE(plan.plan.type, planB.plan.type) = 'PREMIUM' THEN " +
                    "CASE CAST(COALESCE(plan.child.type, planB.child.type) AS string) " +
                    "WHEN 'QUARTERLY' THEN 0 " +
                    "WHEN 'MONTHLY' THEN 1 " +
                    "WHEN 'WEEKLY' THEN 2 " +
                    "WHEN 'DAILY' THEN 3 " +
                    "ELSE 4 " +
                    "END " +
                    "WHEN COALESCE(plan.plan.type, planB.plan.type) = 'ALL_DAY' THEN " +
                    "CASE CAST(COALESCE(plan.child.type, planB.child.type) AS string) " +
                    "WHEN 'QUARTERLY' THEN 5 " +
                    "WHEN 'MONTHLY' THEN 6 " +
                    "WHEN 'WEEKLY' THEN 7 " +
                    "WHEN 'DAILY' THEN 8 " +
                    "ELSE 9 " +
                    "END " +
                    "ELSE 10 " +
                    "END ASC, " +
                    "CASE CAST(p.verification.status AS string) " +
                    "WHEN 'VERIFIED' THEN 0 " +
                    "WHEN 'REQUESTED' THEN 1 " +
                    "WHEN 'ERROR' THEN 2 " +
                    "ELSE 3 END ASC, " +
                    "CASE CAST(ap.tripStatus AS string) " +
                    "WHEN 'ONLINE' THEN 0 " +
                    "WHEN 'REQUESTSHARING' THEN 1 " +
                    "WHEN 'BUSY' THEN 2 " +
                    "WHEN 'OFFLINE' THEN 3 " +
                    "ELSE 4 END ASC, " +
                    "p.rating DESC"
    )
    List<Active> sortByTripStatusWithinDistance(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radius") Double radius,
            @Param("category") SerchCategory category,
            @Param("planStatus") PlanStatus planStatus,
            @Param("tripStatus") TripStatus tripStatus
    );

    @Query(
            value = "SELECT ap FROM Active ap " +
                    "JOIN ap.profile p on p.id = ap.profile.id " +
                    "JOIN Subscription planB on planB.user.id = p.business.id " +
                    "JOIN Subscription plan on plan.user.id = p.id " +
                    "WHERE " +
                    "SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325 < :radius " +
                    "AND p.category = :category " +
                    "AND plan.planStatus = :planStatus " +
                    "GROUP BY ap.id, planB.plan.type, planB.child.type, plan.plan.type, plan.child.type, " +
                    "p.verification.status, ap.latitude, ap.longitude, " +
                    "p.rating, ap.tripStatus " +
                    "ORDER BY " +
                    "SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325 ASC, " +
                    "CASE " +
                    "WHEN COALESCE(plan.plan.type, planB.plan.type) = 'PREMIUM' THEN 0 " +
                    "WHEN COALESCE(plan.plan.type, planB.plan.type) = 'ALL_DAY' THEN 1 " +
                    "ELSE 2 " +
                    "END ASC, " +
                    "CASE " +
                    "WHEN COALESCE(plan.plan.type, planB.plan.type) = 'PREMIUM' THEN " +
                    "CASE CAST(COALESCE(plan.child.type, planB.child.type) AS string) " +
                    "WHEN 'QUARTERLY' THEN 0 " +
                    "WHEN 'MONTHLY' THEN 1 " +
                    "WHEN 'WEEKLY' THEN 2 " +
                    "WHEN 'DAILY' THEN 3 " +
                    "ELSE 4 " +
                    "END " +
                    "WHEN COALESCE(plan.plan.type, planB.plan.type) = 'ALL_DAY' THEN " +
                    "CASE CAST(COALESCE(plan.child.type, planB.child.type) AS string) " +
                    "WHEN 'QUARTERLY' THEN 5 " +
                    "WHEN 'MONTHLY' THEN 6 " +
                    "WHEN 'WEEKLY' THEN 7 " +
                    "WHEN 'DAILY' THEN 8 " +
                    "ELSE 9 " +
                    "END " +
                    "ELSE 10 " +
                    "END ASC, " +
                    "CASE CAST(p.verification.status AS string) " +
                    "WHEN 'VERIFIED' THEN 0 " +
                    "WHEN 'REQUESTED' THEN 1 " +
                    "WHEN 'ERROR' THEN 2 " +
                    "ELSE 3 END ASC, " +
                    "CASE CAST(ap.tripStatus AS string) " +
                    "WHEN 'ONLINE' THEN 0 " +
                    "WHEN 'REQUESTSHARING' THEN 1 " +
                    "WHEN 'BUSY' THEN 2 " +
                    "WHEN 'OFFLINE' THEN 3 " +
                    "ELSE 4 END ASC, " +
                    "p.rating DESC"
    )
    List<Active> sortAllWithinDistance(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radius") Double radius,
            @Param("category") SerchCategory category,
            @Param("planStatus") PlanStatus planStatus
    );

    @Query(
            value = "SELECT ap.* FROM platform.active_providers ap " +
                    "JOIN account.profiles p ON ap.serch_id = p.serch_id " +
                    "JOIN account.business_profiles busi ON p.business_id = busi.serch_id " +
                    "JOIN verified.verification verify ON ap.serch_id = verify.serch_id " +
                    "JOIN verified.verification verifyBusi ON busi.serch_id = verify.serch_id " +
                    "JOIN account.specializations s ON p.serch_id = s.serch_id " +
                    "JOIN company.service_keywords sk ON s.service_id = sk.id " +
                    "JOIN subscription.subscriptions sub ON p.serch_id = sub.serch_id " +
                    "JOIN subscription.subscriptions subBusi ON busi.serch_id = sub.serch_id " +
                    "JOIN company.plan_parents parent ON sub.plan = parent.id " +
                    "JOIN company.plan_children child ON sub.sub_plan = child.id " +
                    "JOIN company.plan_parents parentBusi ON subBusi.plan = parentBusi.id " +
                    "JOIN company.plan_children childBusi ON subBusi.sub_plan = childBusi.id " +
                    "WHERE " +
                    "SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325 < :radius " +
                    "AND (to_tsvector('english', sk.keyword) @@ plainto_tsquery(:query) " +
                    "OR to_tsvector('english', sk.category) @@ plainto_tsquery(:query)) " +
                    "AND (sub.status = :status or subBusi.status = :status) " +
                    "GROUP BY ap.id, parent.type, child.type, parentBusi.type, childBusi.type, " +
                    "sk.keyword, sk.category," +
                    " verify.status, verifyBusi.status, ap.latitude, ap.longitude, ap.status " +
                    "ORDER BY " +
                    "SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325, " +
                    "CASE WHEN (parent.type, parentBusi.type) = 'PREMIUM' THEN 0 " +
                    "WHEN (parent.type, parentBusi.type) = 'ALL_DAY' THEN 1 " +
                    "ELSE 2 END , " +
                    "CASE WHEN (parent.type, parentBusi.type) = 'PREMIUM' THEN " +
                    "CASE coalesce(child.type, childBusi.type) " +
                    "WHEN 'QUARTERLY' THEN 0 " +
                    "WHEN 'MONTHLY' THEN 1 " +
                    "WHEN 'WEEKLY' THEN 2 " +
                    "WHEN 'DAILY' THEN 3 " +
                    "ELSE 4 END " +
                    "WHEN (parent.type, parentBusi.type) = 'ALL_DAY' THEN " +
                    "CASE coalesce(child.type, childBusi.type) " +
                    "WHEN 'QUARTERLY' THEN 5 " +
                    "WHEN 'MONTHLY' THEN 6 " +
                    "WHEN 'WEEKLY' THEN 7 " +
                    "WHEN 'DAILY' THEN 8 " +
                    "ELSE 9 END " +
                    "ELSE 10 END , " +
                    "CASE (verify.status, verifyBusi.status) " +
                    "WHEN 'VERIFIED' THEN 0 " +
                    "WHEN 'REQUESTED' THEN 1 " +
                    "WHEN 'ERROR' THEN 2 " +
                    "ELSE 3 END, " +
                    "CASE ap.status " +
                    "WHEN 'ONLINE' THEN 0 " +
                    "WHEN 'REQUESTSHARING' THEN 1 " +
                    "WHEN 'BUSY' THEN 2 " +
                    "WHEN 'OFFLINE' THEN 3 " +
                    "ELSE 4 END , " +
                    "GREATEST(ts_rank_cd(to_tsvector('english', sk.keyword), plainto_tsquery(:query)), " +
                    "ts_rank_cd(to_tsvector('english', sk.category), plainto_tsquery(:query))) DESC",
            nativeQuery = true
    )
    List<Active> fullTextSearchWithinDistance(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("query") String query,
            @Param("radius") Double radius,
            @Param("status") String planStatus
    );

    @Query(
            value = "SELECT ap.* FROM platform.active_providers ap " +
                    "JOIN account.profiles p ON ap.serch_id = p.serch_id " +
                    "JOIN account.business_profiles busi ON p.business_id = busi.serch_id " +
                    "JOIN verified.verification verify ON ap.serch_id = verify.serch_id " +
                    "JOIN verified.verification verifyBusi ON busi.serch_id = verify.serch_id " +
                    "JOIN account.specializations s ON p.serch_id = s.serch_id " +
                    "JOIN company.service_keywords sk ON s.service_id = sk.id " +
                    "JOIN subscription.subscriptions sub ON p.serch_id = sub.serch_id " +
                    "JOIN subscription.subscriptions subBusi ON busi.serch_id = sub.serch_id " +
                    "JOIN company.plan_parents parent ON sub.plan = parent.id " +
                    "JOIN company.plan_children child ON sub.sub_plan = child.id " +
                    "JOIN company.plan_parents parentBusi ON subBusi.plan = parentBusi.id " +
                    "JOIN company.plan_children childBusi ON subBusi.sub_plan = childBusi.id " +
                    "WHERE " +
                    "SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325 < :radius " +
                    "AND (to_tsvector('english', sk.keyword) @@ plainto_tsquery(:query) " +
                    "OR to_tsvector('english', sk.category) @@ plainto_tsquery(:query)) " +
                    "AND (sub.status = :status or subBusi.status = :status) " +
                    "GROUP BY ap.id, parent.type, child.type, parentBusi.type, childBusi.type," +
                    " sk.keyword, sk.category," +
                    " verify.status, verifyBusi.status, ap.latitude, ap.longitude, ap.status " +
                    "ORDER BY " +
                    "SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325, " +
                    "CASE WHEN (parent.type, parentBusi.type) = 'PREMIUM' THEN 0 " +
                    "WHEN (parent.type, parentBusi.type) = 'ALL_DAY' THEN 1 " +
                    "ELSE 2 END , " +
                    "CASE WHEN (parent.type, parentBusi.type) = 'PREMIUM' THEN " +
                    "CASE coalesce(child.type, childBusi.type) " +
                    "WHEN 'QUARTERLY' THEN 0 " +
                    "WHEN 'MONTHLY' THEN 1 " +
                    "WHEN 'WEEKLY' THEN 2 " +
                    "WHEN 'DAILY' THEN 3 " +
                    "ELSE 4 END " +
                    "WHEN (parent.type, parentBusi.type) = 'ALL_DAY' THEN " +
                    "CASE coalesce(child.type, childBusi.type) " +
                    "WHEN 'QUARTERLY' THEN 5 " +
                    "WHEN 'MONTHLY' THEN 6 " +
                    "WHEN 'WEEKLY' THEN 7 " +
                    "WHEN 'DAILY' THEN 8 " +
                    "ELSE 9 END " +
                    "ELSE 10 END , " +
                    "CASE (verify.status, verifyBusi.status) " +
                    "WHEN 'VERIFIED' THEN 0 " +
                    "WHEN 'REQUESTED' THEN 1 " +
                    "WHEN 'ERROR' THEN 2 " +
                    "ELSE 3 END, " +
                    "CASE ap.status " +
                    "WHEN 'ONLINE' THEN 0 " +
                    "WHEN 'REQUESTSHARING' THEN 1 " +
                    "WHEN 'BUSY' THEN 2 " +
                    "WHEN 'OFFLINE' THEN 3 " +
                    "ELSE 4 END , " +
                    "GREATEST(ts_rank_cd(to_tsvector('english', sk.keyword), plainto_tsquery(:query)), " +
                    "ts_rank_cd(to_tsvector('english', sk.category), plainto_tsquery(:query))) DESC " +
                    "LIMIT 1",
            nativeQuery = true
    )
    Active findBestMatchWithQuery(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("query") String query,
            @Param("radius") Double radius,
            @Param("status") String planStatus
    );

    @Query(
            value = "SELECT ap.* FROM platform.active_providers ap " +
                    "JOIN account.profiles p ON ap.serch_id = p.serch_id " +
                    "JOIN account.business_profiles busi ON p.business_id = busi.serch_id " +
                    "JOIN verified.verification verify ON ap.serch_id = verify.serch_id " +
                    "JOIN verified.verification verifyBusi ON busi.serch_id = verify.serch_id " +
                    "JOIN account.specializations s ON p.serch_id = s.serch_id " +
                    "JOIN company.service_keywords sk ON s.service_id = sk.id " +
                    "JOIN subscription.subscriptions sub ON p.serch_id = sub.serch_id " +
                    "JOIN subscription.subscriptions subBusi ON busi.serch_id = sub.serch_id " +
                    "JOIN company.plan_parents parent ON sub.plan = parent.id " +
                    "JOIN company.plan_children child ON sub.sub_plan = child.id " +
                    "JOIN company.plan_parents parentBusi ON subBusi.plan = parentBusi.id " +
                    "JOIN company.plan_children childBusi ON subBusi.sub_plan = childBusi.id " +
                    "WHERE " +
                    "SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325 < :radius " +
                    "AND (sub.status = :status or subBusi.status = :status) " +
                    "AND p.serch_category = :category " +
                    "GROUP BY ap.id, parent.type, child.type, parentBusi.type, childBusi.type, " +
                    "sk.keyword, sk.category, ap.status," +
                    " verify.status, verifyBusi.status, ap.latitude, ap.longitude " +
                    "ORDER BY " +
                    "SQRT(POWER(:latitude - ap.latitude, 2) + POWER(:longitude - ap.longitude, 2)) * 111.325, " +
                    "CASE WHEN (parent.type, parentBusi.type) = 'PREMIUM' THEN 0 " +
                    "WHEN (parent.type, parentBusi.type) = 'ALL_DAY' THEN 1 " +
                    "ELSE 2 END , " +
                    "CASE WHEN (parent.type, parentBusi.type) = 'PREMIUM' THEN " +
                    "CASE coalesce(child.type, childBusi.type) " +
                    "WHEN 'QUARTERLY' THEN 0 " +
                    "WHEN 'MONTHLY' THEN 1 " +
                    "WHEN 'WEEKLY' THEN 2 " +
                    "WHEN 'DAILY' THEN 3 " +
                    "ELSE 4 END " +
                    "WHEN (parent.type, parentBusi.type) = 'ALL_DAY' THEN " +
                    "CASE coalesce(child.type, childBusi.type) " +
                    "WHEN 'QUARTERLY' THEN 5 " +
                    "WHEN 'MONTHLY' THEN 6 " +
                    "WHEN 'WEEKLY' THEN 7 " +
                    "WHEN 'DAILY' THEN 8 " +
                    "ELSE 9 END " +
                    "ELSE 10 END , " +
                    "CASE (verify.status, verifyBusi.status) " +
                    "WHEN 'VERIFIED' THEN 0 " +
                    "WHEN 'REQUESTED' THEN 1 " +
                    "WHEN 'ERROR' THEN 2 " +
                    "ELSE 3 END, " +
                    "CASE ap.status " +
                    "WHEN 'ONLINE' THEN 0 " +
                    "WHEN 'REQUESTSHARING' THEN 1 " +
                    "WHEN 'BUSY' THEN 2 " +
                    "WHEN 'OFFLINE' THEN 3 " +
                    "ELSE 4 END " +
                    "LIMIT 1",
            nativeQuery = true
    )
    Active findBestMatchWithCategory(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("category") String category,
            @Param("radius") Double radius,
            @Param("status") String planStatus
    );
}