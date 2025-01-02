package com.serch.server.repositories.company;

import com.serch.server.models.company.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

public interface IssueRepository extends JpaRepository<Issue, String> {
    @Query("select i from Issue i where i.speakWithSerch.ticket = ?1  order by i.updatedAt desc")
    Page<Issue> findBySpeakWithSerchTicket(@NonNull String ticket, Pageable pageable);
}