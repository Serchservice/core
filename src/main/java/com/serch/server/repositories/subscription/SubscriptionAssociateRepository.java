package com.serch.server.repositories.subscription;

import com.serch.server.models.subscription.SubscriptionAssociate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionAssociateRepository extends JpaRepository<SubscriptionAssociate, Long> {
}