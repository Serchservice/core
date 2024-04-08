package com.serch.server.repositories.company;

import com.serch.server.models.company.RequestState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestStateRepository extends JpaRepository<RequestState, Long> {
}