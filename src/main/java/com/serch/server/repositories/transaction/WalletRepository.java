package com.serch.server.repositories.transaction;

import com.serch.server.models.transaction.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    boolean existsByProfile_SerchId(@NonNull UUID serchId);

    boolean existsByBusinessProfile_SerchId(@NonNull UUID serchId);
}