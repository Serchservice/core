package com.serch.server.repositories.shared;

import com.serch.server.models.shared.SharedStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface SharedStatusRepository extends JpaRepository<SharedStatus, Long> {
    @Query("""
            select s from SharedStatus s inner join s.sharedLink.guests guests
            where s.sharedLink.id = ?1 and guests.id = ?2 and s.isExpired = ?3""")
    Optional<SharedStatus> findBySharedGuestAndExpired(@NonNull String id, @NonNull String id1, @NonNull Boolean isExpired);
}