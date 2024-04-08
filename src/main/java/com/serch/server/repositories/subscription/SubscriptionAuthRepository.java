package com.serch.server.repositories.subscription;

import com.serch.server.models.subscription.SubscriptionAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubscriptionAuthRepository extends JpaRepository<SubscriptionAuth, UUID> {
}