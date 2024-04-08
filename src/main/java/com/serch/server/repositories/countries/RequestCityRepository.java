package com.serch.server.repositories.countries;

import com.serch.server.models.countries.RequestCity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestCityRepository extends JpaRepository<RequestCity, Long> {
}