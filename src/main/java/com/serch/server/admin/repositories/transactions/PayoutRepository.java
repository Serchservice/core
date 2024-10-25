package com.serch.server.admin.repositories.transactions;

import com.serch.server.admin.models.transaction.Payout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface PayoutRepository extends JpaRepository<Payout, Long> {
  Optional<Payout> findByTransaction_Id(@NonNull String id);
}