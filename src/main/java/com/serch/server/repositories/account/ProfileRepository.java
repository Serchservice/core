package com.serch.server.repositories.account;

import com.serch.server.models.account.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Optional<Profile> findByUser_EmailAddress(@NonNull String emailAddress);
    List<Profile> findByBusiness_Id(@NonNull UUID id);
}