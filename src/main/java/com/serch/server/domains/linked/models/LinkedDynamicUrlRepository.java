package com.serch.server.domains.linked.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface LinkedDynamicUrlRepository extends JpaRepository<LinkedDynamicUrl, Long> {
    Optional<LinkedDynamicUrl> findByIdentifier(@NonNull String identifier);
}