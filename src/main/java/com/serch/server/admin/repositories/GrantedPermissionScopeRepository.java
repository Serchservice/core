package com.serch.server.admin.repositories;

import com.serch.server.admin.enums.PermissionScope;
import com.serch.server.admin.models.GrantedPermissionScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface GrantedPermissionScopeRepository extends JpaRepository<GrantedPermissionScope, Long> {
    Optional<GrantedPermissionScope> findByAccountAndAdmin_Id(@NonNull String account, @NonNull UUID id);
    Optional<GrantedPermissionScope> findByScopeAndAdmin_Id(@NonNull PermissionScope scope, @NonNull UUID id);
}