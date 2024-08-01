package com.serch.server.repositories.transaction;

import com.serch.server.models.transaction.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    Optional<Transaction> findByReference(@NonNull String reference);
    List<Transaction> findByAccount(@NonNull String account);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE (t.account = :id) AND t.status = 'SUCCESSFUL' AND t.createdAt >= :startOfDay AND t.createdAt <= :endOfDay")
    BigDecimal totalToday(@Param("id") String id, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("select t from Transaction t where t.sender = ?1 or t.account = ?1")
    List<Transaction> findBySenderOrReceiver(@NonNull String account);

    @Query("select t from Transaction t where t.type = 'SCHEDULE' and t.status = 'PENDING'")
    List<Transaction> findPendingSchedules();

    @Query("select t from Transaction t where t.sender = ?1 or t.account = ?1")
    Page<Transaction> findRecentBySenderOrReceiver(@NonNull String account, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE (t.account = :userId OR t.sender = :userId) AND t.createdAt BETWEEN :startDate AND :endDate")
    List<Transaction> findByUserAndDateRange(@Param("userId") String userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    Optional<Transaction> findByEvent(@NonNull String event);
}