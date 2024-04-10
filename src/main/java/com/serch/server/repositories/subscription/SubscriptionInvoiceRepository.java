package com.serch.server.repositories.subscription;

import com.serch.server.models.subscription.SubscriptionInvoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionInvoiceRepository extends JpaRepository<SubscriptionInvoice, Long> {
}