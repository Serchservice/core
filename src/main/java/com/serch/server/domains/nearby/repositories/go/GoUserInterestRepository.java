package com.serch.server.domains.nearby.repositories.go;

import com.serch.server.domains.nearby.models.go.user.GoUserInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface GoUserInterestRepository extends JpaRepository<GoUserInterest, Long> {
  List<GoUserInterest> findByUser_Id(@NonNull UUID id);

  @Query("SELECT ui.interest.id FROM GoUserInterest ui WHERE ui.user.id = :userId")
  Set<Long> findInterestIdsByUser_Id(@Param("userId") UUID userId);

  @Query("select gi.interest.id from GoUserInterest gi where gi.user.id = :userId")
  List<Long> findInterestIdsByUserId(@Param("userId") UUID userId);

  /**
   * Count all users who have the specified interest.
   * @param id The ID of the interest.
   * @return The total number of users with the interest.
   */
  @Query("SELECT COUNT(gu) FROM GoUserInterest gu WHERE gu.interest.id = :id")
  long countByInterestId(@Param("id") Long id);

  /**
   * Count users nearby who share the same interest using Haversine formula.
   * @param id The ID of the interest.
   * @param lat The latitude of the location.
   * @param lng The longitude of the location.
   * @param radius The radius within which to search (in kilometers).
   * @return The number of nearby users with the same interest.
   */
  @Query("""
        SELECT COUNT(gu)
        FROM GoUserInterest gu
        WHERE gu.interest.id = :id
        AND gu.user.location IS NOT NULL
        AND gu.user.location.latitude IS NOT NULL
        AND gu.user.location.longitude IS NOT NULL
        and :lng is not null
        and :lat is not null
        and :radius is not null
        AND cast(
            ST_DistanceSphere(
                ST_SetSRID(ST_MakePoint(gu.user.location.longitude, gu.user.location.latitude), 4326),
                ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
            )
        AS double) <= cast(:radius * 1000 as double)
    """)
  long countNearbyUsersWithInterest(@Param("id") Long id, @Param("lat") double lat, @Param("lng") double lng, @Param("radius") double radius);

    Optional<GoUserInterest> findByInterest_IdAndUser_Id(@NonNull Long id, @NonNull UUID id1);

  @Query("""
      select u from GoUserInterest u
      where u.user.location is not null
      and u.interest.id = :interest
      and u.user.id <> :user
      and :lng is not null
      and :lat is not null
      AND cast(
          ST_DistanceSphere(
              ST_SetSRID(ST_MakePoint(u.user.location.longitude, u.user.location.latitude), 4326),
              ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
          )
      AS double) <= cast(u.user.searchRadius * 1000 as double)
  """)
  List<GoUserInterest> findNearbyUsersWithSameInterest(double lat, double lng, long interest, UUID user);

  @Query("""
      select u from GoUserInterest u
      where u.user.location is not null
      and u.interest.id = :interest
      and u.user.id not in :ids
      and :lng is not null
      and :lat is not null
      AND cast(
          ST_DistanceSphere(
              ST_SetSRID(ST_MakePoint(u.user.location.longitude, u.user.location.latitude), 4326),
              ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
          )
      AS double) <= cast(u.user.searchRadius * 1000 as double)
  """)
  List<GoUserInterest> findNearbyUsersWithSameInterestNotAttendingEvent(double lat, double lng, long interest, List<UUID> ids);

  @Query("select (count(g) > 0) from GoUserInterest g where g.user.id = ?1")
  boolean existsByUser_Id(@NonNull UUID id);

  @Modifying
  @Transactional
  @Query("delete from GoUserInterest g where g.interest.id = ?1 and g.user.id = ?2")
  void deleteByInterestAndUser(@NonNull Long interest, @NonNull UUID user);
}