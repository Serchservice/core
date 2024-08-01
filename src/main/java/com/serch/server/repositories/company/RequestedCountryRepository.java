package com.serch.server.repositories.company;

import com.serch.server.models.company.RequestedCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface RequestedCountryRepository extends JpaRepository<RequestedCountry, Long> {
    Optional<RequestedCountry> findByNameIgnoreCase(@NonNull String name);

    boolean existsByNameIgnoreCase(@NonNull String name);
}