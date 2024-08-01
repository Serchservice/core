package com.serch.server.repositories.auth;

import com.serch.server.models.auth.Session;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {
    List<Session> findByUser_Id(@NonNull UUID id);

    @Query("SELECT COUNT(s) FROM Session s WHERE s.user.id = :userId AND s.createdAt BETWEEN :startDate AND :endDate")
    long countByUserAndDateRange(@Param("userId") UUID userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(DISTINCT s.device) FROM Session s WHERE s.user.id = :userId AND s.createdAt BETWEEN :startDate AND :endDate")
    long countDistinctDevicesByUserAndDateRange(@Param("userId") UUID userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM Session s WHERE s.user.id = :user ORDER BY s.createdAt DESC")
    List<Session> findMostRecentSessionByUser(@NonNull UUID user, Pageable pageable);
}