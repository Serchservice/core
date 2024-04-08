package com.serch.server.repositories.subscription;

import com.serch.server.models.subscription.SubscriptionAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionAuthRepository extends JpaRepository<SubscriptionAuth, UUID> {
    Optional<SubscriptionAuth> findBySubscription_Id(@NonNull String id);
}