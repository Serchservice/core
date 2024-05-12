package com.serch.server.repositories.certificate;

import com.serch.server.models.certificate.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface CertificateRepository extends JpaRepository<Certificate, String> {
  Optional<Certificate> findByUser(@NonNull UUID user);
}