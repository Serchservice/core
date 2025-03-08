package com.serch.server.domains.nearby.repositories;

import com.serch.server.domains.nearby.models.NearbyCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface NearbyCategoryRepository extends JpaRepository<NearbyCategory, Long> {
  Optional<NearbyCategory> findByTypeIgnoreCase(@NonNull String type);
}