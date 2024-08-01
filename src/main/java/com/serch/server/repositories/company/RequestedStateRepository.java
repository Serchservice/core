package com.serch.server.repositories.company;

import com.serch.server.models.company.RequestedState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestedStateRepository extends JpaRepository<RequestedState, Long> {
}