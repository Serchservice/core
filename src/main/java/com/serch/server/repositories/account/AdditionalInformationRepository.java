package com.serch.server.repositories.account;

import com.serch.server.models.account.AdditionalInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdditionalInformationRepository extends JpaRepository<AdditionalInformation, Long> {
}