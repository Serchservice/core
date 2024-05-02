package com.serch.server.repositories.business;

import com.serch.server.models.business.BusinessSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface BusinessSubscriptionRepository extends JpaRepository<BusinessSubscription, Long> {
    Optional<BusinessSubscription> findByProfile_Id(@NonNull UUID id);
}