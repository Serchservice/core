package com.serch.server.repositories.transaction;

import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.models.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    Optional<Transaction> findByReference(@NonNull String reference);
    List<Transaction> findBySender_User_Id(@NonNull UUID id);
    List<Transaction> findByAccount(@NonNull String account);
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE (t.account = :id) " +
            "AND t.status = :status " +
            "AND t.createdAt >= :startOfDay AND t.createdAt <= :endOfDay"
    )
    BigDecimal totalToday(
            @Param("id") String id,
            @Param("status") TransactionStatus status,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
}