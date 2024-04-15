package com.serch.server.repositories.trip;

import com.serch.server.models.trip.Active;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActiveRepository extends JpaRepository<Active, Long> {
}