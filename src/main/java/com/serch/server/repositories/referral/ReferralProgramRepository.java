package com.serch.server.repositories.referral;

import com.serch.server.models.referral.ReferralProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface ReferralProgramRepository extends JpaRepository<ReferralProgram, UUID> {
    Optional<ReferralProgram> findByReferralCode(@NonNull String referralCode);
    Optional<ReferralProgram> findByReferLink(@NonNull String referLink);
    Optional<ReferralProgram> findByUser_Id(@NonNull UUID id);
}