package com.serch.server.repositories.shared;

import com.serch.server.models.shared.SharedLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SharedLinkRepository extends JpaRepository<SharedLink, String> {
    Optional<SharedLink> findByLink(@NonNull String link);
    List<SharedLink> findByGuests_Id(@NonNull String id);
    @Query("select s from SharedLink s where s.user.id = ?1 or s.provider.id = ?1")
    List<SharedLink> findByUserId(@NonNull UUID id);
}