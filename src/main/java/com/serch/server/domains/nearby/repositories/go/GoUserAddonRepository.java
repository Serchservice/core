package com.serch.server.domains.nearby.repositories.go;

import com.serch.server.domains.nearby.models.go.user.GoUserAddon;
import com.serch.server.enums.nearby.GoUserAddonStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GoUserAddonRepository extends JpaRepository<GoUserAddon, Long> {
  @Query("select g from GoUserAddon g where g.plan.addon.id = ?1 and g.user.id = ?2")
  Optional<GoUserAddon> findExisting(@NonNull Long id, @NonNull UUID id1);

  @Transactional
  @Modifying
  @Query("delete from GoUserAddon g where g.plan.addon.id = ?1 and g.user.id = ?2")
  void deleteByPlanAndUser(@NonNull Long addonId, @NonNull UUID user);

  List<GoUserAddon> findByNextBillingDateAndIsRecurringTrue(@NonNull LocalDate nextBillingDate);

  List<GoUserAddon> findByStatusAndNextBillingDateBefore(GoUserAddonStatus status, @NonNull LocalDate date);

  List<GoUserAddon> findByUser_Id(@NonNull UUID id);

  @Query("select g from GoUserAddon g where g.user.id = ?1")
  List<GoUserAddon> findSubscribed(UUID id);
}