package com.serch.server.repositories.subscription;

import com.serch.server.models.subscription.SubscriptionInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

public interface SubscriptionInvoiceRepository extends JpaRepository<SubscriptionInvoice, Long> {
    List<SubscriptionInvoice> findBySubscription_User_Id(@NonNull UUID id);
}