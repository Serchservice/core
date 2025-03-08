package com.serch.server.domains.nearby.repositories.go;

import com.serch.server.domains.nearby.models.go.addon.GoAddon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoAddonRepository extends JpaRepository<GoAddon, Long> {
}