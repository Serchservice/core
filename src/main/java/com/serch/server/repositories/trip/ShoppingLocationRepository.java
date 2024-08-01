package com.serch.server.repositories.trip;

import com.serch.server.models.trip.ShoppingLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingLocationRepository extends JpaRepository<ShoppingLocation, Long> {
}