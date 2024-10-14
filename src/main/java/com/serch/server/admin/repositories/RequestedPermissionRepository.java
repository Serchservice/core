package com.serch.server.admin.repositories;

import com.serch.server.admin.enums.Permission;
import com.serch.server.admin.enums.PermissionScope;
import com.serch.server.admin.models.RequestedPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RequestedPermissionRepository extends JpaRepository<RequestedPermission, Long> {
    @Query("select r from RequestedPermission r where r.requestedBy.admin.id = ?1 or r.updatedBy.id = ?1")
    List<RequestedPermission> findByAdmin_Admin_Id(@NonNull UUID id);

    @Query("select r from RequestedPermission r where r.requestedBy.id = ?1 and r.scope = ?2 and (?3 is null or r.account = ?3) and r.permission = ?4")
    Optional<RequestedPermission> findGranted(@NonNull UUID id, @NonNull PermissionScope scope, String account, @NonNull Permission permission);

    @Query("select r from RequestedPermission r where r.expirationPeriod is not null and r.status = 'GRANTED' and r.grantedAt is not null ")
    List<RequestedPermission> findGrantedPermissionsWithExpirationTime();
}