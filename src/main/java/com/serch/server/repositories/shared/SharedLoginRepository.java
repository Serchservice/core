package com.serch.server.repositories.shared;

import com.serch.server.models.shared.SharedLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface SharedLoginRepository extends JpaRepository<SharedLogin, Long> {
  Optional<SharedLogin> findBySharedLink_IdAndGuest_Id(@NonNull String link, @NonNull String id);
  List<SharedLogin> findByGuest_Id(@NonNull String id);
}