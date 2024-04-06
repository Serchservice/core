package com.serch.server.repositories.subscription;

import com.serch.server.models.subscription.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByEmailAddress(String emailAddress);
}