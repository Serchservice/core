package com.serch.server.repositories.auth.incomplete;

import com.serch.server.models.auth.incomplete.IncompleteSpecialty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncompleteSpecialtyRepository extends JpaRepository<IncompleteSpecialty, Long> {
}