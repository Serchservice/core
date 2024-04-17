package com.serch.server.repositories.trip;

import com.serch.server.models.trip.TripTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripTimeRepository extends JpaRepository<TripTime, Long> {
}