package com.serch.server.repositories.subscription;

import com.serch.server.models.subscription.SubscriptionRequestAssociate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRequestAssociateRepository extends JpaRepository<SubscriptionRequestAssociate, Long> {
}