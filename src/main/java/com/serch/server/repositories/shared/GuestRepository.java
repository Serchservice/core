package com.serch.server.repositories.shared;

import com.serch.server.models.shared.Guest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest, String> {
    Optional<Guest> findByEmailAddressIgnoreCase(@NonNull String emailAddress);

    @Query("SELECT sl.guest FROM SharedLogin sl WHERE sl.createdAt <= ?1")
    List<Guest> findGuestsWithLastTripOneYearAgo(ZonedDateTime oneYearAgo);

    @Query("SELECT COUNT(c) FROM Guest c WHERE c.createdAt BETWEEN ?1 AND ?2")
    long countByDateRange(ZonedDateTime startDate, ZonedDateTime endDate);

    List<Guest> findByCreatedAtBetween(ZonedDateTime start, ZonedDateTime end);

    long countByLastNameStartingWithIgnoreCase(@NonNull String start);

    Page<Guest> findByLastNameStartingWithIgnoreCase(@NonNull String start, Pageable pageable);

    @Query("SELECT g FROM Guest g WHERE " +
            "(LOWER(g.firstName) LIKE LOWER(CONCAT(:q, '%')) " +
            "OR LOWER(g.lastName) LIKE LOWER(CONCAT(:q, '%')) " +
            "OR LOWER(g.emailAddress) LIKE LOWER(CONCAT(:q, '%')) " +
            "OR CAST(g.id AS string) LIKE CONCAT(:q, '%'))")
    Page<Guest> searchByQuery(String q, Pageable pageable);

    @Query("SELECT COUNT(g) FROM Guest g WHERE " +
            "(LOWER(g.firstName) LIKE LOWER(CONCAT(:q, '%')) " +
            "OR LOWER(g.lastName) LIKE LOWER(CONCAT(:q, '%')) " +
            "OR LOWER(g.emailAddress) LIKE LOWER(CONCAT(:q, '%')) " +
            "OR CAST(g.id AS string) LIKE CONCAT(:q, '%'))")
    long countByQuery(String q);
}