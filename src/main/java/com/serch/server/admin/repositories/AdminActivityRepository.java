package com.serch.server.admin.repositories;

import com.serch.server.admin.models.AdminActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

public interface AdminActivityRepository extends JpaRepository<AdminActivity, Long> {
  @Query("select a from AdminActivity a where (a.admin.admin.id = ?1 or a.admin.id = ?1 or a.associated = ?2)")
  List<AdminActivity> findByAdminId(@NonNull UUID id, @NonNull String pass);
  @Query("select a from AdminActivity a where (a.admin.admin.id = ?1 or a.admin.id = ?1 or a.associated = ?2) and (a.admin.user.role = 'TEAM' or a.admin.user.role = 'MANAGER' or a.admin.user.role = 'ADMIN')")
  List<AdminActivity> findAdmin(@NonNull UUID id, @NonNull String pass);
  @Query("select a from AdminActivity a where (a.admin.admin.id = ?1 or a.admin.id = ?1 or a.associated = ?2) and (a.admin.user.role = 'TEAM' or a.admin.user.role = 'MANAGER')")
  List<AdminActivity> findManager(@NonNull UUID id, @NonNull String pass);
}