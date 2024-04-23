package com.serch.server.repositories.company;

import com.serch.server.models.company.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

public interface IssueRepository extends JpaRepository<Issue, String> {
    List<Issue> findByUser_Id(@NonNull UUID id);
}