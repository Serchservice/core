package com.serch.server.repositories.auth.incomplete;

import com.serch.server.models.auth.incomplete.IncompletePhoneInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncompletePhoneInformationRepository extends JpaRepository<IncompletePhoneInformation, Long> {
}