package com.serch.server.admin.repositories.transactions;

import com.serch.server.admin.models.transaction.ResolvedTransaction;
import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.enums.transaction.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface ResolvedTransactionRepository extends JpaRepository<ResolvedTransaction, Long> {
    Optional<ResolvedTransaction> findByTransaction_Id(@NonNull String id);

    @Query("select t from ResolvedTransaction t where t.transaction.id = ?1 or t.transaction.account = ?1 or t.transaction.sender = ?1")
    Page<ResolvedTransaction> findByQuery(String query, Pageable pageable);

    @Query("select count(t) from ResolvedTransaction t where t.transaction.id = ?1 or t.transaction.account = ?1 or t.transaction.sender = ?1")
    long countByQuery(String query);

    @Query("SELECT COUNT(t) FROM ResolvedTransaction t WHERE t.createdAt BETWEEN :startDate AND :endDate AND (t.status = :status or t.transaction.status = :status)")
    long countByCreatedAtBetweenAndStatus(
            @Param("startDate") ZonedDateTime startDate,
            @Param("endDate") ZonedDateTime endDate,
            @Param("status") TransactionStatus status
    );

    @Query("SELECT MIN(t.createdAt) FROM ResolvedTransaction t")
    ZonedDateTime findEarliestTransactionDate();

    Page<ResolvedTransaction> findAllByCreatedAtBetweenAndStatus(ZonedDateTime startDate, ZonedDateTime endDate, TransactionStatus status, Pageable pageable);

    @Query("SELECT COUNT(t) FROM ResolvedTransaction t WHERE t.transaction.type = :type AND (t.status = :status or t.transaction.status = :status)")
    long countByTypeAndStatus(@Param("type") TransactionType type, @Param("status") TransactionStatus status);

    Page<ResolvedTransaction> findAllByTransaction_TypeAndStatus(TransactionType type, TransactionStatus status, Pageable pageable);
}