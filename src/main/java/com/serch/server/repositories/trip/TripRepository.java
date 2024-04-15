package com.serch.server.repositories.trip;

import com.serch.server.models.trip.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, String> {
    @Query("select t from Trip t where " +
            "t.provider.emailAddress = ?1 or t.invitedProvider.emailAddress = ?1 " +
            "or t.user.emailAddress = ?1 or t.pricing.guest.emailAddress = ?1"
    )
    List<Trip> findByEmailAddressIgnoreCase(@NonNull String emailAddress);
    @Query("select t from Trip t where " +
            "t.provider.id = ?1 or t.invitedProvider.id = ?1 " +
            "or t.user.id = ?1"
    )
    List<Trip> findByUserId(@NonNull UUID id);
}