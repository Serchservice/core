package com.serch.server.domains.nearby.repositories.go;

import com.serch.server.domains.nearby.models.go.interest.GoInterestCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoInterestCategoryRepository extends JpaRepository<GoInterestCategory, Long> {
}