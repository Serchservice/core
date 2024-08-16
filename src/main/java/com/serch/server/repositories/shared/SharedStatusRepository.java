package com.serch.server.repositories.shared;

import com.serch.server.models.shared.SharedStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedStatusRepository extends JpaRepository<SharedStatus, Long> {
}