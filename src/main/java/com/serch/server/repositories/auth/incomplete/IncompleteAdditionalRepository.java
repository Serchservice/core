package com.serch.server.repositories.auth.incomplete;

import com.serch.server.models.auth.incomplete.IncompleteAdditional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncompleteAdditionalRepository extends JpaRepository<IncompleteAdditional, Long> {
}