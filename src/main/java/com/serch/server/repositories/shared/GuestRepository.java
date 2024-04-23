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
    Optional<Guest> findByIdAndSharedLinks_Id(@NonNull String id, @NonNull String id1);
    @Query("SELECT DISTINCT g FROM Guest g " +
            "INNER JOIN g.sharedLinks sl " +
            "WHERE sl.createdAt < :oneYearAgo " +
            "AND NOT EXISTS (SELECT 1 FROM SharedLink sl2 WHERE sl2.createdAt > :oneYearAgo AND g MEMBER OF sl2.guests)")
    List<Guest> findGuestsWithLastSharedTripOneYearAgo(@Param("oneYearAgo") LocalDateTime oneYearAgo);
}