package com.serch.server.repositories.subscription;

import com.serch.server.models.subscription.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {
}