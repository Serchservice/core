package com.serch.server.repositories.countries;

import com.serch.server.models.countries.LaunchedCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface LaunchedCountryRepository extends JpaRepository<LaunchedCountry, Long> {
    Optional<LaunchedCountry> findByNameIgnoreCase(@NonNull String name);
}