package com.serch.server.repositories.account;

import com.serch.server.models.account.AccountRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface AccountRequestRepository extends JpaRepository<AccountRequest, String> {
    Optional<AccountRequest> findByUser_EmailAddress(@NonNull String emailAddress);
}