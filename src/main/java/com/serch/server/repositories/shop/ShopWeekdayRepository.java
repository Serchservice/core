package com.serch.server.repositories.shop;

import com.serch.server.enums.shop.Weekday;
import com.serch.server.models.shop.ShopWeekday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface ShopWeekdayRepository extends JpaRepository<ShopWeekday, Long> {
    Optional<ShopWeekday> findByDayAndShop_Id(@NonNull Weekday day, @NonNull String id);

    Optional<ShopWeekday> findByIdAndShop_Id(@NonNull Long id, @NonNull String id1);
}