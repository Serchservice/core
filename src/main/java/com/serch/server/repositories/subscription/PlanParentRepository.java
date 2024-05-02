package com.serch.server.repositories.subscription;

import com.serch.server.models.subscription.PlanParent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanParentRepository extends JpaRepository<PlanParent, String> {
}