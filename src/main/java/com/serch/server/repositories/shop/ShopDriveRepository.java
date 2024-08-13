package com.serch.server.repositories.shop;

import com.serch.server.models.shop.ShopDrive;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopDriveRepository extends JpaRepository<ShopDrive, Long> {
    List<ShopDrive> findByShop_Id(String id);
}