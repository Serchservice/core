package com.serch.server.repositories.shop;

import com.serch.server.models.shop.ShopService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface ShopServiceRepository extends JpaRepository<ShopService, Long> {
    Optional<ShopService> findByIdAndShop_Id(@NonNull Long id, @NonNull String id1);
}