package com.serch.server.domains.nearby.repositories.go;

import com.serch.server.domains.nearby.models.go.GoLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface GoLocationRepository extends JpaRepository<GoLocation, Long> {
    Optional<GoLocation> findByUser_Id(@NonNull UUID id);

    Optional<GoLocation> findByActivity_Id(@NonNull String id);
}