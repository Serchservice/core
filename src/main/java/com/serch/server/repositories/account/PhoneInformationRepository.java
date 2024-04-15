package com.serch.server.repositories.account;

import com.serch.server.models.account.PhoneInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface PhoneInformationRepository extends JpaRepository<PhoneInformation, Long> {
    Optional<PhoneInformation> findByUser_Id(@NonNull UUID id);
}