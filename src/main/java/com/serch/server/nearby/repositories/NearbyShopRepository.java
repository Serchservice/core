package com.serch.server.nearby.repositories;

import com.serch.server.nearby.models.NearbyShop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NearbyShopRepository extends JpaRepository<NearbyShop, String> {
}