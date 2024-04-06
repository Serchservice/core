package com.serch.server.repositories.auth.mfa;

import com.serch.server.models.auth.mfa.MFARecoveryCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MFARecoveryCodeRepository extends JpaRepository<MFARecoveryCode, Long> {
}