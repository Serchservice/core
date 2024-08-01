package com.serch.server.repositories.trip;

import com.serch.server.models.trip.TripPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface TripPaymentRepository extends JpaRepository<TripPayment, String> {
    Optional<TripPayment> findByReference(@NonNull String reference);
}