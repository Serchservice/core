package com.serch.server.repositories.subscription;

import com.serch.server.models.subscription.SubscriptionAssociate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

public interface SubscriptionAssociateRepository extends JpaRepository<SubscriptionAssociate, Long> {
    List<SubscriptionAssociate> findByProfile_Id(@NonNull UUID id);
}