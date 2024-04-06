package com.serch.server.repositories.auth;

import com.serch.server.models.auth.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {
    List<Session> findByUser_Id(@NonNull UUID id);
}