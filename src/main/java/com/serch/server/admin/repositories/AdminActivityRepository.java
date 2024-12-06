package com.serch.server.admin.repositories;

import com.serch.server.admin.models.AdminActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.UUID;

public interface AdminActivityRepository extends JpaRepository<AdminActivity, Long> {
  @Query("select a from AdminActivity a where (a.admin.id = ?1 or a.associated = ?2)")
  Page<AdminActivity> findByAdminId(@NonNull UUID id, @NonNull String pass, Pageable pageable);

  @Query("select a from AdminActivity a where (a.admin.admin.id = ?1 or a.admin.id = ?1 or a.associated = ?2) and (a.admin.user.role = 'TEAM' or a.admin.user.role = 'MANAGER' or a.admin.user.role = 'ADMIN')")
  Page<AdminActivity> findAdmin(@NonNull UUID id, @NonNull String pass, Pageable pageable);

  @Query("select a from AdminActivity a where (a.admin.admin.id = ?1 or a.admin.id = ?1 or a.associated = ?2) and (a.admin.user.role = 'TEAM' or a.admin.user.role = 'MANAGER')")
  Page<AdminActivity> findManager(@NonNull UUID id, @NonNull String pass, Pageable pageable);
}