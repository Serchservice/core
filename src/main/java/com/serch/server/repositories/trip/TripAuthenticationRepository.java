package com.serch.server.repositories.trip;

import com.serch.server.models.trip.TripAuthentication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripAuthenticationRepository extends JpaRepository<TripAuthentication, Long> {
}