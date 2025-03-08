package com.serch.server.domains.nearby.repositories.go;

import com.serch.server.domains.nearby.models.go.GoBCap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GoBCapRepository extends JpaRepository<GoBCap, String> {
  Optional<GoBCap> findByActivity_Id(@NonNull String id);

  @Query("select g from GoBCap g where g.activity.interest.id = :id and g.activity.user.id = :user")
  Page<GoBCap> findByInterest(@Param("id") Long id, @Param("user") UUID user, @NonNull Pageable pageable);


  @Query("""
        select g from GoBCap g
        where g.activity.interest.id IN :interests
        and g.activity.user.id = :user
    """)
  Page<GoBCap> findByInterests(@Param("interests") List<Long> interests, @Param("user") UUID user, @NonNull Pageable pageable);

  @Query("select g from GoBCap g where g.activity.user.id = :user")
  Page<GoBCap> findAllBCaps(@Param("user") UUID user, @NonNull Pageable pageable);


  @Query("""
        select g from GoBCap g
        where g.activity.interest.id = :id
        AND g.activity.location.latitude IS NOT NULL
        AND g.activity.location.longitude IS NOT NULL
        and ((:user is not null and g.activity.user.id <> :user) or :user is null)
        and :lng is not null
        and :lat is not null
        and :rad is not null
        AND cast(
            ST_DistanceSphere(
                ST_SetSRID(ST_MakePoint(g.activity.location.longitude, g.activity.location.latitude), 4326),
                ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
            )
        AS double) <= cast(:rad * 1000 as double)
    """)
  Page<GoBCap> findByInterest(@Param("user") UUID user, @Param("id") Long id, @Param("lng") double lng, @Param("lat") double lat, @Param("rad") double rad, @NonNull Pageable pageable);


  @Query("""
        select g from GoBCap g
        where g.activity.interest.id IN :interests
        AND g.activity.location.latitude IS NOT NULL
        AND g.activity.location.longitude IS NOT NULL
        and ((:user is not null and g.activity.user.id <> :user) or :user is null)
        and :lng is not null
        and :lat is not null
        and :rad is not null
        AND cast(
            ST_DistanceSphere(
                ST_SetSRID(ST_MakePoint(g.activity.location.longitude, g.activity.location.latitude), 4326),
                ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
            )
        AS double) <= cast(:rad * 1000 as double)
    """)
  Page<GoBCap> findByInterests(@Param("user") UUID user, @Param("interests") List<Long> interests, @Param("lng") double lng, @Param("lat") double lat, @Param("rad") double rad, @NonNull Pageable pageable);

  @Query("""
        select g from GoBCap g
        where g.activity.location.latitude IS NOT NULL
        AND g.activity.location.longitude IS NOT NULL
        and ((:user is not null and g.activity.user.id <> :user) or :user is null)
        and :lng is not null
        and :lat is not null
        and :rad is not null
        AND cast(
            ST_DistanceSphere(
                ST_SetSRID(ST_MakePoint(g.activity.location.longitude, g.activity.location.latitude), 4326),
                ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
            )
        AS double) <= cast(:rad * 1000 as double)
    """)
  Page<GoBCap> findAllBCaps(@Param("user") UUID user, @Param("lng") double lng, @Param("lat") double lat, @Param("rad") double rad, @NonNull Pageable pageable);
}