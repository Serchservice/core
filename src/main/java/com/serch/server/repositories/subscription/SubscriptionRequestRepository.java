package com.serch.server.repositories.subscription;

import com.serch.server.models.subscription.SubscriptionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRequestRepository extends JpaRepository<SubscriptionRequest, Long> {
    Optional<SubscriptionRequest> findByUser_Id(@NonNull UUID id);
    Optional<SubscriptionRequest> findByReferenceAndUser_Id(@NonNull String reference, @NonNull UUID id);
}