package com.serch.server.repositories.shared;

import com.serch.server.models.shared.GuestAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface GuestAuthRepository extends JpaRepository<GuestAuth, Long> {
    Optional<GuestAuth> findByEmailAddressIgnoreCase(@NonNull String emailAddress);
}