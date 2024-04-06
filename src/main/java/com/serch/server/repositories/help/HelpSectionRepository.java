package com.serch.server.repositories.help;

import com.serch.server.models.help.HelpSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface HelpSectionRepository extends JpaRepository<HelpSection, String> {
    Optional<HelpSection> findByCategory_IdAndId(@NonNull String key, @NonNull String key1);
}