package com.serch.server.repositories.transaction;

import com.serch.server.models.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    Optional<Transaction> findByReference(@NonNull String reference);

    List<Transaction> findBySender_User_Id(@NonNull UUID id);

    List<Transaction> findByAccount(@NonNull String account);
}