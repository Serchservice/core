package com.serch.server.repositories.subscription;

import com.serch.server.models.subscription.SubscriptionRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRequestRepository extends JpaRepository<SubscriptionRequest, Long> {
    Optional<SubscriptionRequest> findByEmailAddress(String emailAddress);
}