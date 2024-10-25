package com.serch.server.repositories.transaction;

import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.enums.transaction.TransactionType;
import com.serch.server.models.transaction.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    Optional<Transaction> findByReference(@NonNull String reference);

    List<Transaction> findByAccount(@NonNull String account);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE (t.account = ?1) AND t.status = 'SUCCESSFUL'" +
            " AND (t.type = 'FUNDING' OR (t.type = 'SCHEDULE' AND t.account = ?1) OR (t.type = 'TIP2FIX' AND t.account = ?1)) " +
            "AND t.createdAt >= ?2 AND t.createdAt <= ?3")
    BigDecimal totalToday(String id, ZonedDateTime startOfDay, ZonedDateTime endOfDay);

    @Query("select t from Transaction t where t.sender = ?1 or t.account = ?1")
    List<Transaction> findBySenderOrReceiver(@NonNull String account);

    @Query("select t from Transaction t where t.type = 'SCHEDULE' and t.status = 'PENDING'")
    List<Transaction> findPendingSchedules();

    @Query("select t from Transaction t where t.sender = ?1 or t.account = ?1")
    Page<Transaction> findRecentBySenderOrReceiver(@NonNull String account, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE (t.account = ?1 OR t.sender = ?1) AND t.createdAt BETWEEN ?2 AND ?3")
    List<Transaction> findByUserAndDateRange(String userId, ZonedDateTime startDate, ZonedDateTime endDate);

    Optional<Transaction> findByEvent(@NonNull String event);

    @Query("SELECT t FROM Transaction t WHERE t.status = 'PENDING'")
    List<Transaction> findAllPending();

    @Query("select t from Transaction t where t.sender = ?1 or t.account = ?1 or t.id = ?1")
    Page<Transaction> findByQuery(String query, Pageable pageable);

    @Query("select count(t) from Transaction t where (t.sender = ?1 or t.account = ?1 or t.id = ?1) and t.status = ?2")
    long countByStatus(String query, TransactionStatus status);

    @Query("select count(t) from Transaction t where t.status = ?1")
    long countByStatus(TransactionStatus status);

    @Query("select t from Transaction t where t.type = 'WITHDRAW' ")
    Page<Transaction> findWithdrawals(Pageable pageable);

    @Query("select count(t) from Transaction t where t.type = 'WITHDRAW' and t.status = ?1")
    long countWithdrawals(TransactionStatus status);

    @Query("SELECT MIN(t.createdAt) FROM Transaction t")
    ZonedDateTime findEarliestTransactionDate();

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.createdAt BETWEEN :startDate AND :endDate AND t.status = :status")
    long countByCreatedAtBetweenAndStatus(
            @Param("startDate") ZonedDateTime startDate,
            @Param("endDate") ZonedDateTime endDate,
            @Param("status") TransactionStatus status
    );

    Page<Transaction> findAllByCreatedAtBetweenAndStatus(ZonedDateTime startDate, ZonedDateTime endDate, TransactionStatus status, Pageable pageable);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.type = :type AND t.status = :status")
    long countByTypeAndStatus(@Param("type") TransactionType type, @Param("status") TransactionStatus status);

    Page<Transaction> findAllByTypeAndStatus(TransactionType type, TransactionStatus status, Pageable pageable);

    Page<Transaction> findAllByStatus(TransactionStatus status, Pageable pageable);
}