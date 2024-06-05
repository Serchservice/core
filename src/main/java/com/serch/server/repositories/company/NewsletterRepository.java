package com.serch.server.repositories.company;

import com.serch.server.models.company.Newsletter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface NewsletterRepository extends JpaRepository<Newsletter, Long> {
  Optional<Newsletter> findByEmailAddressIgnoreCase(@NonNull String emailAddress);
}