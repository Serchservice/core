package com.serch.server.repositories.account;

import com.serch.server.models.account.Referral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface ReferralRepository extends JpaRepository<Referral, String> {
    List<Referral> findByReferredBy_EmailAddress(@NonNull String emailAddress);
}