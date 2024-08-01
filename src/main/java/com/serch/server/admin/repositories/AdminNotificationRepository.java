package com.serch.server.admin.repositories;

import com.serch.server.admin.models.AdminNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdminNotificationRepository extends JpaRepository<AdminNotification, Long> {
    List<AdminNotification> findByUser_Id(@NonNull UUID id);
    Optional<AdminNotification> findByIdAndUser_Id(@NonNull Long id, @NonNull UUID id1);
}