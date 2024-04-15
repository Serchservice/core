package com.serch.server.repositories.account;

import com.serch.server.models.account.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    List<Specialty> findByProfile_Id(@NonNull UUID serchId);
    Optional<Specialty> findByIdAndProfile_Id(@NonNull Long id, @NonNull UUID serchId);
    List<Specialty> findByProfile_Business_Id(@NonNull UUID id);
}