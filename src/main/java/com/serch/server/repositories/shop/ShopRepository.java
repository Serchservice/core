package com.serch.server.repositories.shop;

import com.serch.server.enums.shop.Weekday;
import com.serch.server.models.shop.Shop;
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

public interface ShopRepository extends JpaRepository<Shop, String> {
    List<Shop> findByUser_Id(@NonNull UUID id);

    Page<Shop> findByUser_Id(@NonNull UUID id, Pageable pageable);

    @Query(
            value = "SELECT shop.* FROM platform.shops shop " +
                    "LEFT JOIN platform.shop_services serv ON shop.id = serv.shop_id " +
                    "LEFT JOIN identity.users use ON shop.serch_id = use.id " +
                    "LEFT JOIN account.profiles prof ON prof.serch_id = use.id " +
                    "LEFT JOIN account.business_profiles bus ON bus.serch_id = use.id " +
                    "LEFT JOIN identity.verification verify ON use.id = verify.serch_id " +
                    "WHERE " +
                    "to_tsvector('english', COALESCE(serv.service, '')) @@ plainto_tsquery(:query) " +
                    "OR to_tsvector('english', shop.name) @@ plainto_tsquery(:query) " +
                    "OR to_tsvector('english', shop.address) @@ plainto_tsquery(:query) " +
                    "OR to_tsvector('english', shop.category) @@ plainto_tsquery(:query) " +
                    "GROUP BY shop.serch_id, verify.status, shop.latitude, shop.longitude, shop.status, shop.rating, " +
                    "shop.id, shop.created_at, shop.updated_at, address, shop.category, shop.name, shop.phone_number " +
                    "ORDER BY " +
                    "CASE verify.status" +
                    "   WHEN 'VERIFIED' THEN 0 " +
                    "   WHEN 'REQUESTED' THEN 1 " +
                    "   WHEN 'ERROR' THEN 2 " +
                    "   WHEN 'NOT_VERIFIED' THEN 3 " +
                    "END, " +
                    "CASE shop.status WHEN 'OPEN' THEN 0 WHEN 'CLOSED' THEN 1 ELSE 0 END, " +
                    "shop.rating DESC",
            nativeQuery = true
    )
    List<Shop> search(@Param("query") String query);

    @Query(
            value = "SELECT shop.* FROM platform.shops shop " +
                    "LEFT JOIN platform.shop_services serv ON shop.id = serv.shop_id " +
                    "LEFT JOIN identity.users use ON shop.serch_id = use.id " +
                    "LEFT JOIN account.profiles prof ON prof.serch_id = use.id " +
                    "LEFT JOIN account.business_profiles bus ON bus.serch_id = use.id " +
                    "LEFT JOIN identity.verification verify ON use.id = verify.serch_id " +
                    "WHERE " +
                    "((6371000 * acos(cos(radians(:latitude)) * cos(radians(shop.latitude)) * " +
                    "cos(radians(shop.longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
                    "sin(radians(shop.latitude)))) <= :radius) " +
                    "AND (to_tsvector('english', COALESCE(serv.service, '')) @@ plainto_tsquery(:query) " +
                    "OR to_tsvector('english', shop.name) @@ plainto_tsquery(:query) " +
                    "OR to_tsvector('english', shop.address) @@ plainto_tsquery(:query) " +
                    "OR to_tsvector('english', shop.category) @@ plainto_tsquery(:query)) " +
                    "GROUP BY shop.serch_id, verify.status, shop.latitude, shop.longitude, shop.status, shop.rating, " +
                    "shop.id, shop.created_at, shop.updated_at, address, shop.category, shop.name, shop.phone_number " +
                    "ORDER BY " +
                    "((6371000 * acos(cos(radians(:latitude)) * cos(radians(shop.latitude)) * " +
                    "cos(radians(shop.longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
                    "sin(radians(shop.latitude))))), " +
                    "CASE verify.status " +
                    "   WHEN 'VERIFIED' THEN 0 " +
                    "   WHEN 'REQUESTED' THEN 1 " +
                    "   WHEN 'ERROR' THEN 2 " +
                    "   WHEN 'NOT_VERIFIED' THEN 3 " +
                    "END, " +
                    "CASE shop.status WHEN 'OPEN' THEN 0 WHEN 'CLOSED' THEN 1 ELSE 0 END, " +
                    "shop.rating DESC",
            nativeQuery = true
    )
    Page<Shop> findByServiceAndLocation(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("query") String query,
            @Param("radius") Double radius,
            Pageable pageable
    );

    Optional<Shop> findByIdAndUser_Id(@NonNull String id, @NonNull UUID id1);

    @Query("SELECT s FROM Shop s JOIN s.weekdays w WHERE s.status != 'SUSPENDED' AND s.status != 'OPEN' AND w.day = ?1 " +
            "AND w.opening = CURRENT_TIME")
    List<Shop> findShopsWithCurrentOpeningTimeAndDay(Weekday currentDay);

    @Query("SELECT s FROM Shop s JOIN s.weekdays w WHERE s.status != 'SUSPENDED' AND s.status != 'CLOSED' AND w.day = ?1 " +
            "AND w.closing = CURRENT_TIME")
    List<Shop> findShopsWithCurrentClosingTimeAndDay(Weekday currentDay);

    List<Shop> findAllByCreatedAtBetween(ZonedDateTime start, ZonedDateTime localDateTime);

    @Query("select count(s) from Shop s where s.createdAt BETWEEN ?1 AND ?2")
    long countByDateRange(ZonedDateTime startDate, ZonedDateTime endDate);
}