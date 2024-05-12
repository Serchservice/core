package com.serch.server.repositories.subscription;

import com.serch.server.enums.subscription.PlanStatus;
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
    Optional<SubscriptionInvoice> findBySubscription_PlanStatusAndSubscription_User_Id(@NonNull PlanStatus planStatus, @NonNull UUID id);
    @Query("SELECT si FROM SubscriptionInvoice si JOIN FETCH si.associates sa WHERE si.subscription.id = :subscription ORDER BY si.createdAt DESC")
    Optional<SubscriptionInvoice> findLatestInvoiceWithAssociatesBySubscription(@Param("subscription") String subscription);
}