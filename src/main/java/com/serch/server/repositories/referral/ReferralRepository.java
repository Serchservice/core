package com.serch.server.repositories.referral;

import com.serch.server.models.referral.Referral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReferralRepository extends JpaRepository<Referral, String> {
    List<Referral> findByReferredBy_User_EmailAddress(@NonNull String emailAddress);

    Optional<Referral> findByReferral_Id(@NonNull UUID id);
}