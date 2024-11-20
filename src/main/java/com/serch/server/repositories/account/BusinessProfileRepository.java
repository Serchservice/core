package com.serch.server.repositories.account;

import com.serch.server.models.account.BusinessProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BusinessProfileRepository extends JpaRepository<BusinessProfile, UUID> {
    @Query("select b from BusinessProfile b where b.createdAt between ?1 and ?2")
    List<BusinessProfile> findByCreatedAtBetween(ZonedDateTime startDate, ZonedDateTime endDate);

    Optional<BusinessProfile> findByUser_EmailAddress(@NonNull String emailAddress);
}