package com.serch.server.domains.nearby.repositories.go;

import com.serch.server.domains.nearby.models.go.user.GoUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GoUserRepository extends JpaRepository<GoUser, UUID> {
  Optional<GoUser> findByEmailAddressIgnoreCase(String emailAddress);
}