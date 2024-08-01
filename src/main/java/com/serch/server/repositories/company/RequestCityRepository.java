package com.serch.server.repositories.company;

import com.serch.server.models.company.RequestedCity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestCityRepository extends JpaRepository<RequestedCity, Long> {
}