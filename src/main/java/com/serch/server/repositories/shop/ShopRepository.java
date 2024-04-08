package com.serch.server.repositories.shop;

import com.serch.server.models.shop.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

public interface ShopRepository extends JpaRepository<Shop, String> {
    List<Shop> findByUser_Id(@NonNull UUID id);

//    @Query(
//            value = "SELECT s.* FROM platform.shops shop " +
//                    "JOIN platform.shop_services serv ON shop.id = serv.shop_id " +
//                    "LEFT JOIN identity.users use ON shop.serch_id = use.id " +
//                    "LEFT JOIN subscription.subscriptions sub ON sub.serch_id = use.id " +
//                    "LEFT JOIN account.profiles prof ON prof.serch_id = use.id " +
//                    "LEFT JOIN account.business_profiles bus ON bus.serch_id = use.id " +
//                    "WHERE " +
//                    "((6371 * acos(cos(radians(:latitude)) * cos(radians(shop.latitude)) * " +
//                    "cos(radians(shop.longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
//                    "sin(radians(shop.latitude)))) <= :radius) " +
//                    "AND (to_tsvector('english', serv.service) @@ plainto_tsquery(:query) " +
//                    "OR to_tsvector('english', shop.name) @@ plainto_tsquery(:query) " +
//                    "OR to_tsvector('english', shop.address) @@ plainto_tsquery(:query)) " +
//                    "AND sub.status = :planStatus " +
//                    "GROUP BY shop.serch_id, sub.plan, sub.sub_plan, p.profile_verification_status, " +
//                    "shop.latitude, shop.longitude, bp.profile_picture, shop.status, shop.rating " +
//                    "ORDER BY " +
//                    "((6371 * acos(cos(radians(:latitude)) * cos(radians(shop.latitude)) * " +
//                    "cos(radians(shop.longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
//                    "sin(radians(shop.latitude))))), " +
//                    "CASE " +
//                    "   WHEN COALESCE(p.profile_verification_status, bp.profile_picture) = 'VERIFIED' THEN 0 " +
//                    "   WHEN COALESCE(p.profile_verification_status, bp.profile_picture) = 'REQUESTED' THEN 1 " +
//                    "   WHEN COALESCE(p.profile_verification_status, bp.profile_picture) = 'ERROR' THEN 2 " +
//                    "   WHEN COALESCE(p.profile_verification_status, bp.profile_picture) = 'NOT_VERIFIED' THEN 3 " +
//                    "END, " +
//                    "CASE WHEN pl.plan = 'PREMIUM' THEN 0 " +
//                    "WHEN pl.plan = 'ALL_DAY' THEN 1 " +
//                    "ELSE 2 END , " +
//                    "CASE WHEN pl.plan = 'PREMIUM' THEN " +
//                    "CASE pl.sub_plan " +
//                    "WHEN 'QUARTERLY' THEN 0 " +
//                    "WHEN 'MONTHLY' THEN 1 " +
//                    "WHEN 'WEEKLY' THEN 2 " +
//                    "WHEN 'DAILY' THEN 3 " +
//                    "ELSE 4 END " +
//                    "WHEN pl.plan = 'ALL_DAY' THEN " +
//                    "CASE pl.sub_plan " +
//                    "WHEN 'QUARTERLY' THEN 5 " +
//                    "WHEN 'MONTHLY' THEN 6 " +
//                    "WHEN 'WEEKLY' THEN 7 " +
//                    "WHEN 'DAILY' THEN 8 " +
//                    "ELSE 9 END " +
//                    "ELSE 10 END , " +
//                    "CASE WHEN s.status = 'ONLINE' THEN 0 ELSE 1 END, " +
//                    "s.rating DESC",
//            nativeQuery = true
//    )
//    List<Shop> findByServiceAndLocation(
//            @Param("latitude") Double latitude,
//            @Param("longitude") Double longitude,
//            @Param("query") String query,
//            @Param("radius") Double radius,
//            @Param("planStatus") PlanStatus planStatus
//    );
//    @Query(
//            value = "SELECT s.* FROM platform.shop s " +
//                    "JOIN platform.shop_service ss ON s.serch_id = ss.shop_id " +
//                    "LEFT JOIN platform.profiles p ON s.profile_id = p.serch_id " +
//                    "LEFT JOIN business.profiles bp ON s.business_id = bp.serch_id " +
//                    "LEFT JOIN platform.plans pl ON p.serch_id = pl.serch_id OR bp.serch_id = pl.serch_id " +
//                    "WHERE " +
//                    "((6371 * acos(cos(radians(:latitude)) * cos(radians(s.latitude)) * " +
//                    "cos(radians(s.longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
//                    "sin(radians(s.latitude)))) <= :radius) AND pl.plan_status = :planStatus " +
//                    " AND s.category = :category " +
//                    "GROUP BY s.serch_id, pl.plan, pl.sub_plan, p.profile_verification_status, " +
//                    "s.latitude, s.longitude, bp.profile_picture, s.status, s.rating " +
//                    "ORDER BY " +
//                    "((6371 * acos(cos(radians(:latitude)) * cos(radians(s.latitude)) * " +
//                    "cos(radians(s.longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
//                    "sin(radians(s.latitude))))), " +
//                    "CASE " +
//                    "   WHEN COALESCE(p.profile_verification_status, bp.profile_picture) = 'VERIFIED' THEN 0 " +
//                    "   WHEN COALESCE(p.profile_verification_status, bp.profile_picture) = 'REQUESTED' THEN 1 " +
//                    "   WHEN COALESCE(p.profile_verification_status, bp.profile_picture) = 'ERROR' THEN 2 " +
//                    "   WHEN COALESCE(p.profile_verification_status, bp.profile_picture) = 'NOT_VERIFIED' THEN 3 " +
//                    "END, " +
//                    "CASE WHEN pl.plan = 'PREMIUM' THEN 0 " +
//                    "WHEN pl.plan = 'ALL_DAY' THEN 1 " +
//                    "ELSE 2 END , " +
//                    "CASE WHEN pl.plan = 'PREMIUM' THEN " +
//                    "CASE pl.sub_plan " +
//                    "WHEN 'QUARTERLY' THEN 0 " +
//                    "WHEN 'MONTHLY' THEN 1 " +
//                    "WHEN 'WEEKLY' THEN 2 " +
//                    "WHEN 'DAILY' THEN 3 " +
//                    "ELSE 4 END " +
//                    "WHEN pl.plan = 'ALL_DAY' THEN " +
//                    "CASE pl.sub_plan " +
//                    "WHEN 'QUARTERLY' THEN 5 " +
//                    "WHEN 'MONTHLY' THEN 6 " +
//                    "WHEN 'WEEKLY' THEN 7 " +
//                    "WHEN 'DAILY' THEN 8 " +
//                    "ELSE 9 END " +
//                    "ELSE 10 END , " +
//                    "CASE WHEN s.status = 'ONLINE' THEN 0 ELSE 1 END, " +
//                    "s.rating DESC",
//            nativeQuery = true
//    )
//    List<Shop> findByCategoryAndLocation(
//            @Param("latitude") Double latitude,
//            @Param("longitude") Double longitude,
//            @Param("category") SerchCategory category,
//            @Param("radius") Double radius,
//            @Param("planStatus") PlanStatus planStatus
//    );
}