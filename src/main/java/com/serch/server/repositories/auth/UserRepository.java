package com.serch.server.repositories.auth;

import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.auth.Role;
import com.serch.server.models.auth.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailAddressIgnoreCase(String emailAddress);

    long countByRole(@NonNull Role role);

    @Query("SELECT COUNT(c) FROM users c WHERE c.role = ?1 AND c.createdAt BETWEEN ?2 AND ?3")
    long countByRoleAndDateRange(Role role, ZonedDateTime startDate, ZonedDateTime endDate);

    @Query("select count(u) from users u where u.status = ?1 and (u.role = 'ADMIN' or u.role = 'SUPER_ADMIN' or u.role = 'MANAGER' or u.role = 'TEAM')")
    long countAdminByStatus(@NonNull AccountStatus accountStatus);

    List<User> findByRoleAndCreatedAtBetween(Role role, ZonedDateTime start, ZonedDateTime end);

    List<User> findByRole(@NonNull Role role);

    long countByRoleAndLastNameStartingWithIgnoreCase(@NonNull Role role, @NonNull String start);

    @Query("SELECT DISTINCT UPPER(SUBSTRING(u.lastName, 1, 1)) FROM users u WHERE u.role = :role")
    List<String> findDistinctStartingLettersByRole(Role role);

    Page<User> findByRoleAndLastNameStartingWithIgnoreCase(@NonNull Role role, @NonNull String start, Pageable pageable);

    @Query("SELECT u FROM users u WHERE u.role = :role AND " +
            "(LOWER(u.firstName) LIKE LOWER(CONCAT(:q, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT(:q, '%')) " +
            "OR LOWER(u.emailAddress) LIKE LOWER(CONCAT(:q, '%')) " +
            "OR CAST(u.id AS string) LIKE CONCAT(:q, '%'))")
    Page<User> searchByRoleAndQuery(Role role, String q, Pageable pageable);

    @Query("SELECT COUNT(u) FROM users u WHERE u.role = :role AND " +
            "(LOWER(u.firstName) LIKE LOWER(CONCAT(:q, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT(:q, '%')) " +
            "OR LOWER(u.emailAddress) LIKE LOWER(CONCAT(:q, '%')) " +
            "OR CAST(u.id AS string) LIKE CONCAT(:q, '%'))")
    long countByRoleAndQuery(Role role, String q);
}