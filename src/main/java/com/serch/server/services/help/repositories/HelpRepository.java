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
            "CONCAT('/', c.key, '/', s.key, '/', g.key, '/', f.id) AS link " +
            "FROM company.help_faqs f " +
            "JOIN company.help_groups g ON f.group_key = g.key " +
            "JOIN company.help_sections s ON g.section_key = s.key " +
            "JOIN company.help_categories c ON s.category_key = c.key " +
            "WHERE to_tsvector(f.question) @@ plainto_tsquery(:keyword) " +
            "   OR to_tsvector(c.category) @@ plainto_tsquery(:keyword) " +
            "   OR to_tsvector(s.section) @@ plainto_tsquery(:keyword) " +
            "   OR to_tsvector(g.name) @@ plainto_tsquery(:keyword)",
            nativeQuery = true
    )
    List<Object[]> search(@Param("keyword") String keyword);
}