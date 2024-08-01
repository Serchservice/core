package com.serch.server.repositories.trip;

import com.serch.server.models.trip.TripShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TripShareRepository extends JpaRepository<TripShare, Long> {
  Optional<TripShare> findByTrip_IdAndProvider_Id(@NonNull String id, @NonNull UUID id1);

  @Query("SELECT t from TripShare t join t.timelines tt where (t.provider.id = :userId OR t.provider.business.id = :userId) and " +
          "tt.status in (com.serch.server.enums.trip.TripConnectionStatus.COMPLETED)" +
          " AND FUNCTION('DATE',t.updatedAt) = FUNCTION('CURRENT_DATE') order by t.updatedAt desc")
  List<TripShare> todaySharedTrips(@Param("userId") UUID userId);

  Optional<TripShare> findByTrip_IdAndTrip_Account(@NonNull String id, @NonNull String account);

  Optional<TripShare> findByIdAndTrip_Id(@NonNull Long id, @NonNull String id1);
}