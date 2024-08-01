package com.serch.server.repositories.auth.verified;

import com.serch.server.models.auth.verified.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VerificationRepository extends JpaRepository<Verification, UUID> {
}