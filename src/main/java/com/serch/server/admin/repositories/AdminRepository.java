package com.serch.server.admin.repositories;

import com.serch.server.admin.models.Admin;
import com.serch.server.enums.auth.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface AdminRepository extends JpaRepository<Admin, UUID> {
    Optional<Admin> findByUser_EmailAddressIgnoreCase(String emailAddress);
    Optional<Admin> findByPass(@NonNull String pass);
    Optional<Admin> findByUser_Role(@NonNull Role role);
}