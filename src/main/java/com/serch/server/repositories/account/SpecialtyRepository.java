package com.serch.server.repositories.account;

import com.serch.server.models.account.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
}