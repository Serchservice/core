package com.serch.server.repositories.auth;

import com.serch.server.enums.account.AccountStatus;
import com.serch.server.models.auth.AccountStatusTracker;
import com.serch.server.models.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface AccountStatusTrackerRepository extends JpaRepository<AccountStatusTracker, Long> {
    List<AccountStatusTracker> findByUser_IdAndStatusAndCreatedAtBetween(
            UUID user_id,
            AccountStatus status,
            ZonedDateTime createdAt,
            ZonedDateTime createdAt2
    );

    void deleteByUser(@NonNull User user);

    List<AccountStatusTracker> findByUser_Id(@NonNull UUID id);
}