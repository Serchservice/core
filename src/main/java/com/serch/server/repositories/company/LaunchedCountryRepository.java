package com.serch.server.repositories.company;

import com.serch.server.models.company.LaunchedCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface LaunchedCountryRepository extends JpaRepository<LaunchedCountry, Long> {
    Optional<LaunchedCountry> findByNameIgnoreCase(@NonNull String name);
}