package com.serch.server.repositories.auth;

import com.serch.server.models.auth.Pending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface PendingRepository extends JpaRepository<Pending, Long> {
  Optional<Pending> findByUser_Id(@NonNull UUID id);
    Optional<Pending> findByUser_EmailAddressIgnoreCase(@NonNull String emailAddress);
}