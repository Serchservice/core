package com.serch.server.repositories.company;

import com.serch.server.admin.services.projections.MetricProjection;
import com.serch.server.enums.company.IssueStatus;
import com.serch.server.models.company.SpeakWithSerch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface SpeakWithSerchRepository extends JpaRepository<SpeakWithSerch, String> {
    List<SpeakWithSerch> findByUser_Id(@NonNull UUID id);

    List<SpeakWithSerch> findByCreatedAtBefore(@NonNull ZonedDateTime createdAt);

    @Query("SELECT YEAR(s.createdAt) as header, COUNT(s) as count FROM SpeakWithSerch s GROUP BY YEAR(s.createdAt)")
    List<MetricProjection> findSpeakWithSerchMetrics();

    @Query("select s from SpeakWithSerch s where s.assignedAdmin.id = ?1")
    List<SpeakWithSerch> findAssigned(@NonNull UUID id);

    @Query("select s from SpeakWithSerch s where s.assignedAdmin IS NULL OR s.assignedAdmin.id != ?1")
    List<SpeakWithSerch> findOthers(@NonNull UUID id);

    @Query("select count(s) from SpeakWithSerch s where YEAR(s.createdAt) =  ?1 and s.status = ?2")
    long countByYearAndStatus(Integer year, IssueStatus status);

    Page<SpeakWithSerch> findByUser_Id(@NonNull UUID id, Pageable pageable);
}