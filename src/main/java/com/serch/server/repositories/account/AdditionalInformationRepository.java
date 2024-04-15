package com.serch.server.repositories.account;

import com.serch.server.models.account.AdditionalInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdditionalInformationRepository extends JpaRepository<AdditionalInformation, Long> {
    Optional<AdditionalInformation> findByProfile_Id(UUID serchId);
}