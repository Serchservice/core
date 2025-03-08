package com.serch.server.domains.nearby.repositories.go;

import com.serch.server.domains.nearby.models.go.addon.GoAddonPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoAddonPlanRepository extends JpaRepository<GoAddonPlan, String> {
}