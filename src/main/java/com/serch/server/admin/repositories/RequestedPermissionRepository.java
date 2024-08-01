package com.serch.server.admin.repositories;

import com.serch.server.admin.models.RequestedPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

public interface RequestedPermissionRepository extends JpaRepository<RequestedPermission, Long> {
    @Query("select r from RequestedPermission r where r.admin.admin.id = ?1 or r.updatedBy.id = ?1")
    List<RequestedPermission> findByAdmin_Admin_Id(@NonNull UUID id);
}