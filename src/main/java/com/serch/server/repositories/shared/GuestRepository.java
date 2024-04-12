package com.serch.server.repositories.shared;

import com.serch.server.models.shared.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest, String> {
    Optional<Guest> findByEmailAddressIgnoreCase(@NonNull String emailAddress);
}