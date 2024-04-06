package com.serch.server.repositories.account;

import com.serch.server.models.account.Referral;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferralRepository extends JpaRepository<Referral, String> {
}