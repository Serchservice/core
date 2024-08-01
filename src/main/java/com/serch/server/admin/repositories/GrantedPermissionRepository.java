package com.serch.server.admin.repositories;

import com.serch.server.admin.enums.Permission;
import com.serch.server.admin.models.GrantedPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface GrantedPermissionRepository extends JpaRepository<GrantedPermission, Long> {
    Optional<GrantedPermission> findByScope_IdAndPermission(@NonNull Long id, @NonNull Permission permission);
}