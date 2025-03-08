package com.serch.server.domains.nearby.repositories.go;

import com.serch.server.domains.nearby.models.go.activity.GoAttendingUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface GoAttendingUserRepository extends JpaRepository<GoAttendingUser, Long> {
  Optional<GoAttendingUser> findByActivity_IdAndUser_Id(@NonNull String id, @NonNull UUID id1);

  @Query("select count(g) from GoAttendingUser g where g.activity.id = ?1")
  long countByActivity_Id(@NonNull String id);

  @Query("select g from GoAttendingUser g where g.user.id = ?1 and g.activity.status = com.serch.server.enums.trip.TripStatus.CLOSED")
  Page<GoAttendingUser> findAttended(@NonNull UUID id, Pageable pageable);

  @Query("select g from GoAttendingUser g where g.user.id = ?1 and (g.activity.status = com.serch.server.enums.trip.TripStatus.ACTIVE or g.activity.status = com.serch.server.enums.trip.TripStatus.WAITING)")
  Page<GoAttendingUser> findAttending(@NonNull UUID id, Pageable pageable);
}