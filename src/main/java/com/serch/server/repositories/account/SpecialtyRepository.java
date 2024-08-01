package com.serch.server.repositories.account;

import com.serch.server.models.account.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    List<Specialty> findByProfile_Id(@NonNull UUID serchId);
    Optional<Specialty> findByIdAndProfile_Id(@NonNull Long id, @NonNull UUID serchId);
    List<Specialty> findByProfile_Business_Id(@NonNull UUID id);
    @Query(
            value = "SELECT s.* FROM account.specializations s LEFT JOIN account.profiles p ON s.serch_id = p.serch_id " +
                    "WHERE to_tsvector('english', s.specialty) @@ plainto_tsquery(:query) " +
                    "OR to_tsvector('english', COALESCE(p.serch_category, '')) @@ plainto_tsquery(:query) ",
            nativeQuery = true
    )
    List<Specialty> fullTextSearch(@Param("query") String query);
}