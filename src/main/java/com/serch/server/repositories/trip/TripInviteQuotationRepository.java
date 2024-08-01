package com.serch.server.repositories.trip;

import com.serch.server.models.trip.TripInviteQuotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface TripInviteQuotationRepository extends JpaRepository<TripInviteQuotation, Long> {
  Optional<TripInviteQuotation> findByInvite_IdAndProvider_id(@NonNull String id, @NonNull UUID id1);

  Optional<TripInviteQuotation> findByIdAndInvite_Id(@NonNull Long id, @NonNull String id1);

  Optional<TripInviteQuotation> findByIdAndInvite_IdAndInvite_Account(@NonNull Long id, @NonNull String id1, @NonNull String account);
}