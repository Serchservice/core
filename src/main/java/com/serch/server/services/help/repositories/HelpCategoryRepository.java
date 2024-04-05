package com.serch.server.services.help.repositories;

import com.serch.server.services.help.models.HelpCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HelpCategoryRepository extends JpaRepository<HelpCategory, String> {
}