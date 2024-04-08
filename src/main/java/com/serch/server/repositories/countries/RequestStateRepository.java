package com.serch.server.repositories.countries;

import com.serch.server.models.countries.RequestState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestStateRepository extends JpaRepository<RequestState, Long> {
}