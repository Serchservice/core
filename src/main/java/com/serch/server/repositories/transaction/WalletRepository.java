package com.serch.server.repositories.transaction;

import com.serch.server.models.transaction.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, String> {
    Optional<Wallet> findByUser_Id(@NonNull UUID id);

    boolean existsByUser_Id(@NonNull UUID id);
}