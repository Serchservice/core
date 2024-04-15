package com.serch.server.repositories.shop;

import com.serch.server.models.shop.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

public interface ShopRepository extends JpaRepository<Shop, String> {
    List<Shop> findByUser_Id(@NonNull UUID id);

    @Query(
            value = "SELECT shop.* FROM platform.shops shop " +
                    "JOIN platform.shop_services serv ON shop.id = serv.shop_id " +
                    "LEFT JOIN identity.users use ON shop.serch_id = use.id " +
                    "LEFT JOIN subscription.subscriptions sub ON sub.serch_id = use.id " +
                    "JOIN company.plan_parents parent ON sub.plan = parent.id " +
                    "JOIN company.plan_children child ON sub.sub_plan = child.id " +
                    "LEFT JOIN account.profiles prof ON prof.serch_id = use.id " +
                    "LEFT JOIN account.business_profiles bus ON bus.serch_id = use.id " +
                    "JOIN verified.verification verify ON use.id = verify.serch_id " +
                    "WHERE " +
                    "((6371 * acos(cos(radians(:latitude)) * cos(radians(shop.latitude)) * " +
                    "cos(radians(shop.longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
                    "sin(radians(shop.latitude)))) <= :radius) " +
                    "AND (to_tsvector('english', serv.service) @@ plainto_tsquery(:query) " +
                    "OR to_tsvector('english', shop.name) @@ plainto_tsquery(:query) " +
                    "OR to_tsvector('english', shop.address) @@ plainto_tsquery(:query)) " +
                    "AND sub.status = :planStatus " +
                    "GROUP BY shop.serch_id, sub.plan, sub.sub_plan, verify.status, parent.type, child.type, " +
                    "shop.latitude, shop.longitude, shop.status, shop.rating, shop.id, shop.created_at, " +
                    "shop.updated_at, address, shop.category, shop.name, shop.phone_number, shop.place " +
                    "ORDER BY " +
                    "((6371 * acos(cos(radians(:latitude)) * cos(radians(shop.latitude)) * " +
                    "cos(radians(shop.longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
                    "sin(radians(shop.latitude))))), " +
                    "CASE verify.status" +
                    "   WHEN 'VERIFIED' THEN 0 " +
                    "   WHEN 'REQUESTED' THEN 1 " +
                    "   WHEN 'ERROR' THEN 2 " +
                    "   WHEN 'NOT_VERIFIED' THEN 3 " +
                    "END, " +
                    "CASE WHEN parent.type = 'PREMIUM' THEN 0 " +
                    "WHEN parent.type = 'ALL_DAY' THEN 1 " +
                    "ELSE 2 END , " +
                    "CASE WHEN parent.type = 'PREMIUM' THEN " +
                    "CASE child.type " +
                    "WHEN 'QUARTERLY' THEN 0 " +
                    "WHEN 'MONTHLY' THEN 1 " +
                    "WHEN 'WEEKLY' THEN 2 " +
                    "WHEN 'DAILY' THEN 3 " +
                    "ELSE 4 END " +
                    "WHEN parent.type = 'ALL_DAY' THEN " +
                    "CASE child.type " +
                    "WHEN 'QUARTERLY' THEN 5 " +
                    "WHEN 'MONTHLY' THEN 6 " +
                    "WHEN 'WEEKLY' THEN 7 " +
                    "WHEN 'DAILY' THEN 8 " +
                    "ELSE 9 END " +
                    "ELSE 10 END , " +
                    "CASE WHEN shop.status = 'ONLINE' THEN 0 ELSE 1 END, " +
                    "shop.rating DESC",
            nativeQuery = true
    )
    List<Shop> findByServiceAndLocation(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("query") String query,
            @Param("radius") Double radius,
            @Param("planStatus") String planStatus
    );

    @Query(
            value = "SELECT shop.* FROM platform.shops shop " +
                    "JOIN platform.shop_services serv ON shop.id = serv.shop_id " +
                    "LEFT JOIN identity.users use ON shop.serch_id = use.id " +
                    "LEFT JOIN subscription.subscriptions sub ON sub.serch_id = use.id " +
                    "JOIN company.plan_parents parent ON sub.plan = parent.id " +
                    "JOIN company.plan_children child ON sub.sub_plan = child.id " +
                    "LEFT JOIN account.profiles prof ON prof.serch_id = use.id " +
                    "LEFT JOIN account.business_profiles bus ON bus.serch_id = use.id " +
                    "JOIN verified.verification verify ON use.id = verify.serch_id " +
                    "WHERE " +
                    "((6371 * acos(cos(radians(:latitude)) * cos(radians(shop.latitude)) * " +
                    "cos(radians(shop.longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
                    "sin(radians(shop.latitude)))) <= :radius) " +
                    " AND shop.category = :category " +
                    "AND sub.status = :planStatus " +
                    "GROUP BY shop.serch_id, sub.plan, sub.sub_plan, verify.status, parent.type, child.type, " +
                    "shop.latitude, shop.longitude, shop.status, shop.rating, shop.id, shop.created_at, " +
                    "shop.updated_at, address, shop.category " +
                    "ORDER BY " +
                    "((6371 * acos(cos(radians(:latitude)) * cos(radians(shop.latitude)) * " +
                    "cos(radians(shop.longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
                    "sin(radians(shop.latitude))))), " +
                    "CASE verify.status" +
                    "   WHEN 'VERIFIED' THEN 0 " +
                    "   WHEN 'REQUESTED' THEN 1 " +
                    "   WHEN 'ERROR' THEN 2 " +
                    "   WHEN 'NOT_VERIFIED' THEN 3 " +
                    "END, " +
                    "CASE WHEN parent.type = 'PREMIUM' THEN 0 " +
                    "WHEN parent.type = 'ALL_DAY' THEN 1 " +
                    "ELSE 2 END , " +
                    "CASE WHEN parent.type = 'PREMIUM' THEN " +
                    "CASE child.type " +
                    "WHEN 'QUARTERLY' THEN 0 " +
                    "WHEN 'MONTHLY' THEN 1 " +
                    "WHEN 'WEEKLY' THEN 2 " +
                    "WHEN 'DAILY' THEN 3 " +
                    "ELSE 4 END " +
                    "WHEN parent.type = 'ALL_DAY' THEN " +
                    "CASE child.type " +
                    "WHEN 'QUARTERLY' THEN 5 " +
                    "WHEN 'MONTHLY' THEN 6 " +
                    "WHEN 'WEEKLY' THEN 7 " +
                    "WHEN 'DAILY' THEN 8 " +
                    "ELSE 9 END " +
                    "ELSE 10 END , " +
                    "CASE WHEN shop.status = 'ONLINE' THEN 0 ELSE 1 END, " +
                    "shop.rating DESC",
            nativeQuery = true
    )
    List<Shop> findByCategoryAndLocation(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("category") String category,
            @Param("radius") Double radius,
            @Param("planStatus") String planStatus
    );
}