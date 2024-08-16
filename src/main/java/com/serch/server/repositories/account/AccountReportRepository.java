package com.serch.server.repositories.account;

import com.serch.server.models.account.AccountReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

public interface AccountReportRepository extends JpaRepository<AccountReport, String> {
    List<AccountReport> findByAccount(@NonNull String id);

    List<AccountReport> findByUser_Id(@NonNull UUID id);
}