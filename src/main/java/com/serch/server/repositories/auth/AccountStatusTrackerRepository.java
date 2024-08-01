package com.serch.server.repositories.auth;

import com.serch.server.enums.account.AccountStatus;
import com.serch.server.models.auth.AccountStatusTracker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AccountStatusTrackerRepository extends JpaRepository<AccountStatusTracker, Long> {
    List<AccountStatusTracker> findByUser_IdAndStatusAndCreatedAtBetween(
            UUID user_id,
            AccountStatus status,
            LocalDateTime createdAt,
            LocalDateTime createdAt2
    );
}