package com.serch.server.repositories.account;

import com.serch.server.enums.auth.Role;
import com.serch.server.models.account.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Optional<Profile> findByUser_EmailAddress(@NonNull String emailAddress);

    @Query("select b from Profile b where b.createdAt between ?1 and ?2")
    List<Profile> findByCreatedAtBetween(ZonedDateTime startMonth, ZonedDateTime endMonth);

    @Query("select b from Profile b where b.user.role = ?1 and b.createdAt between ?2 and ?3")
    List<Profile> findByRoleAndCreatedAtBetween(Role role, ZonedDateTime startMonth, ZonedDateTime endMonth);
}