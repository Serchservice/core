package com.serch.server.repositories.auth.incomplete;

import com.serch.server.models.auth.incomplete.IncompleteProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncompleteProfileRepository extends JpaRepository<IncompleteProfile, Long> {
}