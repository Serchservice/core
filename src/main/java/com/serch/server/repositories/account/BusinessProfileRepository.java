package com.serch.server.repositories.account;

import com.serch.server.models.account.BusinessProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BusinessProfileRepository extends JpaRepository<BusinessProfile, UUID> {
}