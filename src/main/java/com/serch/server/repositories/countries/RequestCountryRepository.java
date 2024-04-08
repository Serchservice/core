package com.serch.server.repositories.countries;

import com.serch.server.models.countries.RequestCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface RequestCountryRepository extends JpaRepository<RequestCountry, Long> {
    Optional<RequestCountry> findByNameIgnoreCase(@NonNull String name);

    boolean existsByNameIgnoreCase(@NonNull String name);
}