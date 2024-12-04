package com.serch.server.repositories.shared;

import com.serch.server.models.shared.SharedLink;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SharedLinkRepository extends JpaRepository<SharedLink, String> {
    Optional<SharedLink> findByLink(@NonNull String link);

    @Query("select s from SharedLink s where s.user.id = ?1 or s.provider.id = ?1 or s.provider.business.id = ?1")
    List<SharedLink> findByUserId(@NonNull UUID id);

    @Query("select s from SharedLink s where s.user.id = ?1 or s.provider.id = ?1 or s.provider.business.id = ?1")
    Page<SharedLink> findByUserId(@NonNull UUID id, Pageable pageable);

    @Query("SELECT sl FROM SharedLink sl JOIN sl.logins login WHERE login.status = 'USED'")
    List<SharedLink> findSharedLinksWithUsedStatus();

    List<SharedLink> findAllByCreatedAtBetween(ZonedDateTime from, ZonedDateTime to);

    @Query("select count(s) from SharedLink s where s.createdAt BETWEEN ?1 AND ?2")
    long countWithinDateRange(ZonedDateTime start, ZonedDateTime end);

    @Query("select count(s) from SharedLink s where s.id = ?1 and s.createdAt BETWEEN ?2 AND ?3")
    long countByIdAndDateRange(String id, ZonedDateTime start, ZonedDateTime end);

    @Query("select count(s) from SharedLink s where (s.user.id = ?1 or s.provider.id = ?1) and s.createdAt BETWEEN ?2 AND ?3")
    long countByIdAndDateRange(UUID id, ZonedDateTime start, ZonedDateTime end);
}