package com.serch.server.repositories.auth;

import com.serch.server.models.auth.Session;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {
    List<Session> findByUser_Id(@NonNull UUID id);

    @Query("SELECT COUNT(s) FROM Session s WHERE s.user.id = ?1 AND s.createdAt BETWEEN ?2 AND ?3")
    long countByUserAndDateRange(UUID userId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COUNT(DISTINCT s.device) FROM Session s WHERE s.user.id = ?1 AND s.createdAt BETWEEN ?2 AND ?3")
    long countDistinctDevicesByUserAndDateRange(UUID userId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT s FROM Session s WHERE s.user.id = ?1 ORDER BY s.createdAt DESC")
    List<Session> findMostRecentSessionByUser(@NonNull UUID user, Pageable pageable);
}