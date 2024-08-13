package com.serch.server.repositories.trip;

import com.serch.server.models.trip.MapView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MapViewRepository extends JpaRepository<MapView, Long> {
}