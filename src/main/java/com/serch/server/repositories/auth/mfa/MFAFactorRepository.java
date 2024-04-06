package com.serch.server.repositories.auth.mfa;

import com.serch.server.models.auth.mfa.MFAFactor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface MFAFactorRepository extends JpaRepository<MFAFactor, UUID> {
    Optional<MFAFactor> findByUser_Id(@NonNull UUID id);
}