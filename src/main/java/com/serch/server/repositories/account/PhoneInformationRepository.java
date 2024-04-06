package com.serch.server.repositories.account;

import com.serch.server.models.account.PhoneInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneInformationRepository extends JpaRepository<PhoneInformation, Long> {
}