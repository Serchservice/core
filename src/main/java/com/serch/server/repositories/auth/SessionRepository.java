package com.serch.server.repositories.auth;

import com.serch.server.enums.auth.Role;
import com.serch.server.models.auth.Session;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {
    @Query("select count(distinct s.user) from Session s where s.user.role = ?1")
    long countDistinctUsersByRole(@NonNull Role role);

    List<Session> findByUser_Id(@NonNull UUID id);

    @Query("SELECT COUNT(s) FROM Session s WHERE s.user.id = ?1 AND s.createdAt BETWEEN ?2 AND ?3")
    long countByUserAndDateRange(UUID userId, ZonedDateTime startDate, ZonedDateTime endDate);

    @Query("SELECT COUNT(DISTINCT s.device) FROM Session s WHERE s.user.id = ?1 AND s.createdAt BETWEEN ?2 AND ?3")
    long countDistinctDevicesByUserAndDateRange(UUID userId, ZonedDateTime startDate, ZonedDateTime endDate);

    @Query("SELECT s FROM Session s WHERE s.user.id = ?1 ORDER BY s.createdAt DESC")
    List<Session> findMostRecentSessionByUser(@NonNull UUID user, Pageable pageable);

    Optional<Session> findByIdAndUser_Id(@NonNull UUID id, @NonNull UUID id1);

    @Query("select s from Session s where s.user.id = ?1 and (s.ipAddress = ?2 or s.name = ?3)")
    List<Session> findByUser_IdAndIpAddressOrName(@NonNull UUID id, @NonNull String ipAddress, @NonNull String name);

    @Query("select count(distinct s.user) from Session s where s.user.role = ?1 AND s.createdAt BETWEEN ?2 AND ?3")
    long countDistinctUsersByRoleAndDateRange(Role role, ZonedDateTime start, ZonedDateTime end);
}