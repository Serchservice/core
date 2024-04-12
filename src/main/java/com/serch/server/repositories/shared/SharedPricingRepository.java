package com.serch.server.repositories.shared;

import com.serch.server.models.shared.SharedPricing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedPricingRepository extends JpaRepository<SharedPricing, Long> {
}