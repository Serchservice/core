package com.serch.server.repositories.account;

import com.serch.server.models.account.BusinessProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface BusinessProfileRepository extends JpaRepository<BusinessProfile, UUID> {
    Optional<BusinessProfile> findByUser_EmailAddress(@NonNull String emailAddress);
}