package com.serch.server.repositories.auth.incomplete;

import com.serch.server.models.auth.incomplete.Incomplete;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IncompleteRepository extends JpaRepository<Incomplete, Long> {
    Optional<Incomplete> findByEmailAddress(String emailAddress);
}