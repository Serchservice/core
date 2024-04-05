package com.serch.server.services.help.repositories;

import com.serch.server.services.help.models.Help;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface HelpRepository extends JpaRepository<Help, UUID> {
    @Query(value = "SELECT " +
            "f.question AS question," +
            "c.category AS category," +
            "CONCAT(s.section, ' ', '>', ' ', g.name) AS section," +
            "c.image AS image, " +
            "CONCAT('/', c.id, '/', s.id, '/', g.id, '/', f.id) AS link " +
            "FROM company.help_faqs f " +
            "JOIN company.help_groups g ON f.group_id = g.id " +
            "JOIN company.help_sections s ON g.section_id = s.id " +
            "JOIN company.help_categories c ON s.category_id = c.id " +
            "WHERE to_tsvector(f.question) @@ plainto_tsquery(:keyword) " +
            "   OR to_tsvector(c.category) @@ plainto_tsquery(:keyword) " +
            "   OR to_tsvector(s.section) @@ plainto_tsquery(:keyword) " +
            "   OR to_tsvector(g.name) @@ plainto_tsquery(:keyword)",
            nativeQuery = true
    )
    List<Object[]> search(@Param("keyword") String keyword);
}