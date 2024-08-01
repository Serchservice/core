package com.serch.server.repositories.trip;

import com.serch.server.enums.trip.TripConnectionStatus;
import com.serch.server.models.trip.TripTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface TripTimelineRepository extends JpaRepository<TripTimeline, Long> {
  Optional<TripTimeline> findByStatusAndTrip_Id(@NonNull TripConnectionStatus status, @NonNull String id);

  Optional<TripTimeline> findByStatusAndSharing_Id(@NonNull TripConnectionStatus status, @NonNull Long id);

    List<TripTimeline> findByTrip_Id(@NonNull String id);

  List<TripTimeline> findBySharing_Id(@NonNull Long id);
}