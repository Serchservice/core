package com.serch.server.repositories.auth;

import com.serch.server.models.auth.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByIdAndUser_Id(@NonNull UUID id, @NonNull UUID id1);
}