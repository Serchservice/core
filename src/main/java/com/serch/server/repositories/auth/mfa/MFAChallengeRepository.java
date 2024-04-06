package com.serch.server.repositories.auth.mfa;

import com.serch.server.models.auth.mfa.MFAChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MFAChallengeRepository extends JpaRepository<MFAChallenge, UUID> {
}