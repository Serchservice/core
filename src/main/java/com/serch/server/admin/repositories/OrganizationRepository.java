package com.serch.server.admin.repositories;

import com.serch.server.admin.models.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Optional<Organization> findBySecret(@NonNull String secret);

    Optional<Organization> findByUsernameIgnoreCaseOrEmailAddressIgnoreCase(@NonNull String username, @NonNull String emailAddress);
}