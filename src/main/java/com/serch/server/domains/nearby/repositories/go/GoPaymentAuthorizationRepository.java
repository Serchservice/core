package com.serch.server.domains.nearby.repositories.go;

import com.serch.server.domains.nearby.models.go.GoPaymentAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoPaymentAuthorizationRepository extends JpaRepository<GoPaymentAuthorization, Long> {
}