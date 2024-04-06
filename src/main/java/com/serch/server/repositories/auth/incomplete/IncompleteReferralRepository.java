package com.serch.server.repositories.auth.incomplete;

import com.serch.server.models.auth.incomplete.IncompleteReferral;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncompleteReferralRepository extends JpaRepository<IncompleteReferral, Long> {
}