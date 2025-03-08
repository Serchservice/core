package com.serch.server.domains.nearby.repositories.go;

import com.serch.server.domains.nearby.models.go.addon.GoAddonTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface GoAddonTransactionRepository extends JpaRepository<GoAddonTransaction, String> {
  Optional<GoAddonTransaction> findByReference(@NonNull String reference);

  @Query("SELECT t FROM GoAddonTransaction t WHERE t.status = 'PENDING'")
  List<GoAddonTransaction> findAllPending();
}