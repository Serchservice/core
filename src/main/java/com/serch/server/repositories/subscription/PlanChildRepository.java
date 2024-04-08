package com.serch.server.repositories.subscription;

import com.serch.server.models.subscription.PlanChild;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanChildRepository extends JpaRepository<PlanChild, String> {
}