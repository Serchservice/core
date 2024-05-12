package com.serch.server.repositories.company;

import com.serch.server.models.company.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByCreatedAtBefore(@NonNull LocalDateTime createdAt);
}