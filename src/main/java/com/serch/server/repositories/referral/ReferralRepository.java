package com.serch.server.repositories.referral;

import com.serch.server.models.referral.Referral;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReferralRepository extends JpaRepository<Referral, String> {
    List<Referral> findByReferredBy_User_EmailAddress(@NonNull String emailAddress);

    Page<Referral> findByReferredBy_User_EmailAddress(@NonNull String emailAddress, Pageable pageable);

    Page<Referral> findByReferredBy_User_Id(@NonNull UUID id, Pageable pageable);

    Optional<Referral> findByReferral_Id(@NonNull UUID id);
}