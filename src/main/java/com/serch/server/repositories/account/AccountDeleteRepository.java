package com.serch.server.repositories.account;

import com.serch.server.enums.company.IssueStatus;
import com.serch.server.models.account.AccountDelete;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.*;

public interface AccountDeleteRepository extends JpaRepository<AccountDelete, String> {
    Optional<AccountDelete> findByUser_EmailAddress(String emailAddress);

    List<AccountDelete> findByStatusInAndCreatedAtBefore(List<IssueStatus> statuses, ZonedDateTime cutoffDateTime);
}