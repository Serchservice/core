package com.serch.server.repositories.subscription;

import com.serch.server.enums.subscription.PlanType;
import com.serch.server.models.subscription.PlanParent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface PlanParentRepository extends JpaRepository<PlanParent, String> {
    Optional<PlanParent> findByType(@NonNull PlanType type);
}