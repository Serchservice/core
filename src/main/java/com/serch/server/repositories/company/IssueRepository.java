package com.serch.server.repositories.company;

import com.serch.server.models.company.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<Issue, String> {
}