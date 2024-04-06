package com.serch.server.repositories.help;

import com.serch.server.models.help.HelpCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HelpCategoryRepository extends JpaRepository<HelpCategory, String> {
}