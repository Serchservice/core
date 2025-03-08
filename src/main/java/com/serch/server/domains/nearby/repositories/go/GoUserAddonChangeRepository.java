package com.serch.server.domains.nearby.repositories.go;

import com.serch.server.domains.nearby.models.go.user.GoUserAddonChange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoUserAddonChangeRepository extends JpaRepository<GoUserAddonChange, Long> {
}