package com.serch.server.repositories.subscription;

import com.serch.server.models.subscription.SubscriptionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface SubscriptionRequestRepository extends JpaRepository<SubscriptionRequest, Long> {
    Optional<SubscriptionRequest> findByEmailAddress(String emailAddress);

    Optional<SubscriptionRequest> findByReferenceAndEmailAddress(@NonNull String reference, @NonNull String emailAddress);
}