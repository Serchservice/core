package com.serch.server.repositories.company;

import com.serch.server.admin.services.projections.MetricProjection;
import com.serch.server.enums.company.IssueStatus;
import com.serch.server.models.company.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, String> {
    List<Complaint> findByCreatedAtBefore(@NonNull LocalDateTime createdAt);

    @Query("SELECT YEAR(c.createdAt) as header, COUNT(c) as count FROM Complaint c GROUP BY YEAR(c.createdAt)")
    List<MetricProjection> findComplaintMetrics();

    List<Complaint> findByEmailAddress(@NonNull String emailAddress);

    @Query("select count(c) from Complaint c where YEAR(c.createdAt) =  ?1 and c.status = ?2")
    long countByYearAndStatus(@NonNull int year, @NonNull IssueStatus status);
}