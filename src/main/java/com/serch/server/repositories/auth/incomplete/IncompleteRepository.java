package com.serch.server.repositories.auth.incomplete;

import com.serch.server.models.auth.incomplete.Incomplete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IncompleteRepository extends JpaRepository<Incomplete, Long> {
    Optional<Incomplete> findByEmailAddress(String emailAddress);
    List<Incomplete> findByCreatedAtBefore(@NonNull LocalDateTime createdAt);
}