package com.serch.server.repositories.auth.incomplete;

import com.serch.server.models.auth.incomplete.IncompleteCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncompleteCategoryRepository extends JpaRepository<IncompleteCategory, Long> {
}