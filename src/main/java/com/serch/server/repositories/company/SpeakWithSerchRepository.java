package com.serch.server.repositories.company;

import com.serch.server.models.company.SpeakWithSerch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SpeakWithSerchRepository extends JpaRepository<SpeakWithSerch, String> {
    List<SpeakWithSerch> findByUser_Id(@NonNull UUID id);
    List<SpeakWithSerch> findByCreatedAtBefore(@NonNull LocalDateTime createdAt);
}