package com.serch.server.repositories.auth;

import com.serch.server.models.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailAddressIgnoreCase(String emailAddress);
}