package com.serch.server.repositories.subscription;

import com.serch.server.models.subscription.SubscriptionInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionInvoiceRepository extends JpaRepository<SubscriptionInvoice, Long> {
    List<SubscriptionInvoice> findBySubscription_User_Id(@NonNull UUID id);
    @Query("SELECT s FROM SubscriptionInvoice s WHERE s.subscription.user.id = :userId AND s.status = 'ACTIVE'")
    Optional<SubscriptionInvoice> findActiveByUser(@Param("userId") UUID id);
}