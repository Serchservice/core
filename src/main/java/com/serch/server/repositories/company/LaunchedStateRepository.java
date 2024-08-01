package com.serch.server.repositories.company;

import com.serch.server.models.company.LaunchedState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LaunchedStateRepository extends JpaRepository<LaunchedState, Long> {
}