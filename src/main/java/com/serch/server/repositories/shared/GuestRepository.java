package com.serch.server.repositories.shared;

import com.serch.server.models.shared.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest, String> {
    Optional<Guest> findByEmailAddressIgnoreCase(@NonNull String emailAddress);
    @Query("SELECT sl.guest FROM SharedLogin sl " +
            "WHERE sl.createdAt <= :oneYearAgo")
    List<Guest> findGuestsWithLastTripOneYearAgo(@Param("oneYearAgo") LocalDateTime oneYearAgo);

    List<Guest> findByCountryLikeIgnoreCase(@NonNull String country);

    List<Guest> findByStateLikeIgnoreCase(@NonNull String state);
}