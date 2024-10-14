package com.serch.server.admin.repositories;

import com.serch.server.admin.enums.Permission;
import com.serch.server.admin.enums.PermissionScope;
import com.serch.server.admin.models.GrantedPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface GrantedPermissionRepository extends JpaRepository<GrantedPermission, Long> {
    Optional<GrantedPermission> findByScope_IdAndPermission(@NonNull Long id, @NonNull Permission permission);

    @Query("select g from GrantedPermission g where g.permission = ?1 and g.scope.scope = ?2 and g.scope.admin.id = ?3 and (?4 is null or g.scope.account = ?4)")
    Optional<GrantedPermission> findExisting(@NonNull Permission permission, @NonNull PermissionScope scope, @NonNull UUID id, String account);
}