package com.serch.server.repositories.subscription;

import com.serch.server.models.subscription.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {
    Optional<Subscription> findByUser_Id(@NonNull UUID id);
    @Query("select s from Subscription s where s.retries < 3 and s.status = 'ACTIVE'")
    List<Subscription> findExpired();
}