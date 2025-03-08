package com.serch.server.domains.nearby.repositories;

import com.serch.server.domains.nearby.models.NearbyShop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NearbyShopRepository extends JpaRepository<NearbyShop, String> {
}