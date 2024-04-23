package com.serch.server.repositories.account;

import com.serch.server.models.account.AccountSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface AccountSettingRepository extends JpaRepository<AccountSetting, Long> {
    Optional<AccountSetting> findByUser_Id(@NonNull UUID id);
}