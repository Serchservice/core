package com.serch.server.domains.nearby.repositories;

import com.serch.server.domains.nearby.models.Nearby;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface NearbyRepository extends JpaRepository<Nearby, UUID> {
  Optional<Nearby> findByFcmTokenIgnoreCaseOrId(@NonNull String fcmToken, UUID id);
  Optional<Nearby> findByFcmTokenIgnoreCase(@NonNull String fcmToken);
}