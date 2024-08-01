package com.serch.server.repositories.company;

import com.serch.server.models.company.ServiceSuggest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceSuggestRepository extends JpaRepository<ServiceSuggest, String> {
}