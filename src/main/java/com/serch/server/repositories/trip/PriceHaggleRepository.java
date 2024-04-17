package com.serch.server.repositories.trip;

import com.serch.server.models.trip.PriceHaggle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface PriceHaggleRepository extends JpaRepository<PriceHaggle, UUID> {
    Optional<PriceHaggle> findBySharedStatus_Id(@NonNull Long id);
}