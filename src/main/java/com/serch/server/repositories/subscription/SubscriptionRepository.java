package com.serch.server.repositories.subscription;

import com.serch.server.models.subscription.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUser_Id(@NonNull UUID id);
}