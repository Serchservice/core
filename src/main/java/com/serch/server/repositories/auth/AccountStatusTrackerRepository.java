package com.serch.server.repositories.auth;

import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.auth.Role;
import com.serch.server.models.auth.AccountStatusTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    List<AccountStatusTracker> findByUser_Id(@NonNull UUID id);

    @Query("select b from AccountStatusTracker b where b.user.role = ?1 and b.createdAt between ?2 and ?3")
    List<AccountStatusTracker> findByRoleAndCreatedAtBetween(Role role, ZonedDateTime startMonth, ZonedDateTime endMonth);
}