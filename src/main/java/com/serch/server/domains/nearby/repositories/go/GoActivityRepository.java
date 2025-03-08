package com.serch.server.domains.nearby.repositories.go;

import com.serch.server.domains.nearby.models.go.activity.GoActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface GoActivityRepository extends JpaRepository<GoActivity, String> {
    @Query("""
        select g from GoActivity g
        where CAST(g.date AS DATE) = :timestamp
        and g.interest.id = :id
        and g.user.id = :user
    """)
    Page<GoActivity> findByTimestampAndInterest(@Param("timestamp") LocalDate timestamp, @Param("id") Long id, @Param("user") UUID user, @NonNull Pageable pageable);

    @Query("select g from GoActivity g where g.interest.id = :id and g.user.id = :user")
    Page<GoActivity> findByInterest(@Param("id") Long id, @Param("user") UUID user, @NonNull Pageable pageable);

    @Query("select g from GoActivity g where CAST(g.date AS DATE) = :timestamp and g.user.id = :user")
    Page<GoActivity> findByTimestamp(@Param("timestamp") LocalDate timestamp, @Param("user") UUID user, @NonNull Pageable pageable);

    @Query("select g from GoActivity g where g.user.id = :user")
    Page<GoActivity> getHistory(@Param("user") UUID user, @NonNull Pageable pageable);

    @Query("select g from GoActivity g where g.interest.id IN :interests and g.user.id = :user and g.status = com.serch.server.enums.trip.TripStatus.ACTIVE")
    Page<GoActivity> findOngoingByInterests(@Param("interests") List<Long> interests, @Param("user") UUID user, @NonNull Pageable pageable);

    @Query("select g from GoActivity g where g.user.id = :user and g.status = com.serch.server.enums.trip.TripStatus.ACTIVE")
    Page<GoActivity> findOngoing(@Param("user") UUID user, @NonNull Pageable pageable);

    @Query("select g from GoActivity g where g.interest.id IN :interests and g.user.id = :user and g.status = com.serch.server.enums.trip.TripStatus.WAITING")
    Page<GoActivity> findUpcomingByInterests(@Param("interests") List<Long> interests, @Param("user") UUID user, @NonNull Pageable pageable);

    @Query("select g from GoActivity g where g.user.id = :user and g.status = com.serch.server.enums.trip.TripStatus.WAITING")
    Page<GoActivity> findUpcoming(@Param("user") UUID user, @NonNull Pageable pageable);

    @Query("""
        select g from GoActivity g
        where g.status <> com.serch.server.enums.trip.TripStatus.CLOSED
        AND CAST(g.date AS DATE) = :timestamp
        and g.interest.id = :id
        AND g.location.latitude IS NOT NULL
        AND g.location.longitude IS NOT NULL
        and :lng is not null
        and :lat is not null
        and :rad is not null
        and ((:user is not null and g.user.id <> :user) or :user is null)
        AND (g.date > CURRENT_DATE OR (g.date = CURRENT_DATE AND g.endTime >= CURRENT_TIME))
        AND cast(
            ST_DistanceSphere(
                ST_SetSRID(ST_MakePoint(g.location.longitude, g.location.latitude), 4326),
                ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
            )
        AS double) <= cast(:rad * 1000 as double)
    """)
    Page<GoActivity> findByTimestampAndInterest(@Param("user") UUID user, @Param("timestamp") LocalDate timestamp, @Param("id") Long id, @Param("lng") double lng, @Param("lat") double lat, @Param("rad") double rad, @NonNull Pageable pageable);

    @Query("""
        select g from GoActivity g
        where g.status <> com.serch.server.enums.trip.TripStatus.CLOSED
        and g.interest.id = :id
        AND g.location.latitude IS NOT NULL
        AND g.location.longitude IS NOT NULL
        and :lng is not null
        and :lat is not null
        and :rad is not null
        and ((:user is not null and g.user.id <> :user) or :user is null)
        AND (g.date > CURRENT_DATE OR (g.date = CURRENT_DATE AND g.endTime >= CURRENT_TIME))
        AND cast(
            ST_DistanceSphere(
                ST_SetSRID(ST_MakePoint(g.location.longitude, g.location.latitude), 4326),
                ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
            )
        AS double) <= cast(:rad * 1000 as double)
    """)
    Page<GoActivity> findByInterest(@Param("user") UUID user, @Param("id") Long id, @Param("lng") double lng, @Param("lat") double lat, @Param("rad") double rad, @NonNull Pageable pageable);

    @Query("""
        select g from GoActivity g
        where g.status <> com.serch.server.enums.trip.TripStatus.CLOSED
        AND CAST(g.date AS DATE) = :timestamp
        AND g.location.latitude IS NOT NULL
        AND g.location.longitude IS NOT NULL
        and :lng is not null
        and :lat is not null
        and :rad is not null
        and ((:user is not null and g.user.id <> :user) or :user is null)
        AND (g.date > CURRENT_DATE OR (g.date = CURRENT_DATE AND g.endTime >= CURRENT_TIME))
        AND cast(
            ST_DistanceSphere(
                ST_SetSRID(ST_MakePoint(g.location.longitude, g.location.latitude), 4326),
                ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
            )
        AS double) <= cast(:rad * 1000 as double)
    """)
    Page<GoActivity> findByTimestamp(@Param("user") UUID user, @Param("timestamp") LocalDate timestamp, @Param("lng") double lng, @Param("lat") double lat, @Param("rad") double rad, @NonNull Pageable pageable);

    @Query("""
        select g from GoActivity g
        where g.status = com.serch.server.enums.trip.TripStatus.ACTIVE
        and g.interest.id IN :interests
        AND g.location.latitude IS NOT NULL
        AND g.location.longitude IS NOT NULL
        and :lng is not null
        and :lat is not null
        and :rad is not null
        and ((:user is not null and g.user.id <> :user) or :user is null)
        AND (g.date > CURRENT_DATE OR (g.date = CURRENT_DATE AND g.endTime >= CURRENT_TIME))
        AND cast(
            ST_DistanceSphere(
                ST_SetSRID(ST_MakePoint(g.location.longitude, g.location.latitude), 4326),
                ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
            )
        AS double) <= cast(:rad * 1000 as double)
    """)
    Page<GoActivity> findOngoingByInterests(@Param("user") UUID user, @Param("interests") List<Long> interests, @Param("lng") double lng, @Param("lat") double lat, @Param("rad") double rad, @NonNull Pageable pageable);

    @Query("""
        select g from GoActivity g
        where g.status = com.serch.server.enums.trip.TripStatus.ACTIVE
        AND g.location.latitude IS NOT NULL
        AND g.location.longitude IS NOT NULL
        and :lng is not null
        and :lat is not null
        and :rad is not null
        and ((:user is not null and g.user.id <> :user) or :user is null)
        AND (g.date > CURRENT_DATE OR (g.date = CURRENT_DATE AND g.endTime >= CURRENT_TIME))
        AND cast(
            ST_DistanceSphere(
                ST_SetSRID(ST_MakePoint(g.location.longitude, g.location.latitude), 4326),
                ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
            )
        AS double) <= cast(:rad * 1000 as double)
    """)
    Page<GoActivity> findOngoing(@Param("user") UUID user, @Param("lng") double lng, @Param("lat") double lat, @Param("rad") double rad, @NonNull Pageable pageable);

    @Query("""
        select g from GoActivity g
        where g.status = com.serch.server.enums.trip.TripStatus.WAITING
        and g.interest.id IN :interests
        AND g.location.latitude IS NOT NULL
        AND g.location.longitude IS NOT NULL
        and :lng is not null
        and :lat is not null
        and :rad is not null
        and ((:user is not null and g.user.id <> :user) or :user is null)
        AND (g.date > CURRENT_DATE OR (g.date = CURRENT_DATE AND g.endTime >= CURRENT_TIME))
        AND cast(
            ST_DistanceSphere(
                ST_SetSRID(ST_MakePoint(g.location.longitude, g.location.latitude), 4326),
                ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
            )
        AS double) <= cast(:rad * 1000 as double)
    """)
    Page<GoActivity> findUpcomingByInterests(@Param("user") UUID user, @Param("interests") List<Long> interests, @Param("lng") double lng, @Param("lat") double lat, @Param("rad") double rad, @NonNull Pageable pageable);

    @Query("""
        select g from GoActivity g
        where g.status = com.serch.server.enums.trip.TripStatus.WAITING
        AND g.location.latitude IS NOT NULL
        AND g.location.longitude IS NOT NULL
        and :lng is not null
        and :lat is not null
        and :rad is not null
        and ((:user is not null and g.user.id <> :user) or :user is null)
        AND (g.date > CURRENT_DATE OR (g.date = CURRENT_DATE AND g.endTime >= CURRENT_TIME))
        AND cast(
            ST_DistanceSphere(
                ST_SetSRID(ST_MakePoint(g.location.longitude, g.location.latitude), 4326),
                ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
            )
        AS double) <= cast(:rad * 1000 as double)
    """)
    Page<GoActivity> findUpcoming(@Param("user") UUID user, @Param("lng") double lng, @Param("lat") double lat, @Param("rad") double rad, @NonNull Pageable pageable);

    @Query("""
        select g from GoActivity g
        where g.status <> com.serch.server.enums.trip.TripStatus.CLOSED
        and g.interest.id IN :interests
        AND g.location.latitude IS NOT NULL
        AND g.location.longitude IS NOT NULL
        and :lng is not null
        and :lat is not null
        and :rad is not null
        and ((:user is not null and g.user.id <> :user) or :user is null)
        AND (g.date > CURRENT_DATE OR (g.date = CURRENT_DATE AND g.endTime >= CURRENT_TIME))
        AND cast(
            ST_DistanceSphere(
                ST_SetSRID(ST_MakePoint(g.location.longitude, g.location.latitude), 4326),
                ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
            )
        AS double) <= cast(:rad * 1000 as double)
    """)
    Page<GoActivity> findByInterests(@Param("user") UUID user, @Param("interests") List<Long> interests, @Param("lng") double lng, @Param("lat") double lat, @Param("rad") double rad, @NonNull Pageable pageable);

    @Query("""
        select g from GoActivity g
        where g.status <> com.serch.server.enums.trip.TripStatus.CLOSED
        AND g.location.latitude IS NOT NULL
        AND g.location.longitude IS NOT NULL
        and :lng is not null
        and :lat is not null
        and :rad is not null
        and ((:user is not null and g.user.id <> :user) or :user is null)
        AND (g.date > CURRENT_DATE OR (g.date = CURRENT_DATE AND g.endTime >= CURRENT_TIME))
        AND cast(
            ST_DistanceSphere(
                ST_SetSRID(ST_MakePoint(g.location.longitude, g.location.latitude), 4326),
                ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
            )
        AS double) <= cast(:rad * 1000 as double)
    """)
    Page<GoActivity> getHistory(@Param("user") UUID user, @Param("lng") double lng, @Param("lat") double lat, @Param("rad") double rad, @NonNull Pageable pageable);

    @Query("select g from GoActivity g where (CAST(g.date as date) < CURRENT_DATE) and g.status <> com.serch.server.enums.trip.TripStatus.CLOSED")
    List<GoActivity> getHistory();

    @Query(value = """
        select g.* from nearby.go_activities g
        where (CAST(g.date as date) = CURRENT_DATE)
        and g.start_time between (CURRENT_TIME - interval '30 seconds') and (CURRENT_TIME + interval '30 seconds')
        and g.status <> 'CLOSED'
    """, nativeQuery = true)
    List<GoActivity> getStartingNow();

    @Query(value = """
        select g.* from nearby.go_activities g
        where (CAST(g.date as date) = CURRENT_DATE)
        and g.end_time between (CURRENT_TIME - interval '30 seconds') and (CURRENT_TIME + interval '30 seconds')
        and g.status <> 'CLOSED'
    """, nativeQuery = true)
    List<GoActivity> getEndingNow();

    @Query(value = """
        select g.* from nearby.go_activities g
        where CAST(g.date as date) = CURRENT_DATE - interval '1 day'
    """, nativeQuery = true)
    List<GoActivity> getYesterday();

    // Method to check if there are any older events by the user with the same interest
    @Query("SELECT COUNT(e) > 0 FROM GoActivity e WHERE e.user.id = :user AND e.interest.id = :interest AND e.id <> :eventId")
    boolean existsByCreatorWithSameInterest(UUID user, Long interest, String eventId);

    // Method to check if there are any older events by other users with the same interest
    @Query("SELECT COUNT(e) > 0 FROM GoActivity e WHERE e.user.id <> :user AND e.interest.id = :interest")
    boolean existsByOtherCreatorsWithSameInterest(UUID user, Long interest);

    // Method to retrieve all older events created by the user with the same interest
    @Query("SELECT e FROM GoActivity e WHERE e.user.id = :user AND e.interest.id = :interest AND e.id <> :eventId")
    Page<GoActivity> findRelated(UUID user, Long interest, String eventId, Pageable pageable);

    // Method to retrieve all older events not created by the user with the same interest
    @Query("SELECT e FROM GoActivity e WHERE e.user.id <> :user AND e.interest.id = :interest AND e.id <> :eventId")
    Page<GoActivity> findSimilar(UUID user, Long interest, String eventId, Pageable pageable);

    @Query(
            value = """
                select g.* from nearby.go_activities g
                LEFT JOIN nearby.go_users gu ON g.user_id = gu.id
                LEFT JOIN nearby.go_locations gul ON gu.id = gul.user_id
                LEFT JOIN nearby.go_interests gi ON g.interest_id = gi.id
                LEFT JOIN nearby.go_interest_categories gic ON gi.category_id = gic.id
                WHERE
                SQRT(POWER(:lat - gul.latitude, 2) + POWER(:lng - gul.longitude, 2)) * 111.325 < :rad / 1000
                AND (to_tsvector('english', COALESCE(gi.name, '')) @@ plainto_tsquery(:q)
                OR to_tsvector('english', COALESCE(gi.emoji, '')) @@ plainto_tsquery(:q)
                OR to_tsvector('english', COALESCE(gic.name, '')) @@ plainto_tsquery(:q)
                OR to_tsvector('english', COALESCE(gu.first_name, '')) @@ plainto_tsquery(:q)
                OR to_tsvector('english', COALESCE(gu.last_name, '')) @@ plainto_tsquery(:q))
                and g.status <> 'CLOSED'
                GROUP BY g.id, gi.name, gi.emoji, gic.name, gul.latitude, gul.longitude, g.status, gu.first_name, gu.last_name
                ORDER BY SQRT(POWER(:lat - gul.latitude, 2) + POWER(:lng - gul.longitude, 2)) * 111.325,
                CASE g.status
                WHEN 'ACTIVE' THEN 0 ELSE 1 END,
                GREATEST(ts_rank_cd(to_tsvector('english', COALESCE(gi.name, '')), plainto_tsquery(:q)),
                ts_rank_cd(to_tsvector('english', COALESCE(gi.emoji, '')), plainto_tsquery(:q)),
                ts_rank_cd(to_tsvector('english', COALESCE(gic.name, '')), plainto_tsquery(:q)),
                ts_rank_cd(to_tsvector('english', COALESCE(gu.first_name, '')), plainto_tsquery(:q)),
                ts_rank_cd(to_tsvector('english', COALESCE(gu.last_name, '')), plainto_tsquery(:q))) DESC
            """,
            nativeQuery = true
    )
    Page<GoActivity> search(String q, Double lat, Double lng, Double rad, Pageable pageable);

    @Query(
            value = """
                select g.* from nearby.go_activities g
                LEFT JOIN nearby.go_users gu ON g.user_id = gu.id
                LEFT JOIN nearby.go_locations gul ON gu.id = gul.user_id
                LEFT JOIN nearby.go_interests gi ON g.interest_id = gi.id
                LEFT JOIN nearby.go_interest_categories gic ON gi.category_id = gic.id
                WHERE
                SQRT(POWER(:lat - gul.latitude, 2) + POWER(:lng - gul.longitude, 2)) * 111.325 < :rad / 1000
                AND (to_tsvector('english', COALESCE(gi.name, '')) @@ plainto_tsquery(:q)
                OR to_tsvector('english', COALESCE(gi.emoji, '')) @@ plainto_tsquery(:q)
                OR to_tsvector('english', COALESCE(gic.name, '')) @@ plainto_tsquery(:q)
                OR to_tsvector('english', COALESCE(gu.first_name, '')) @@ plainto_tsquery(:q)
                OR to_tsvector('english', COALESCE(gu.last_name, '')) @@ plainto_tsquery(:q))
                and g.status <> 'CLOSED'
                and gi.id IN :ids
                GROUP BY g.id, gi.name, gi.emoji, gic.name, gul.latitude, gul.longitude, g.status, gu.first_name, gu.last_name, gi.id
                ORDER BY SQRT(POWER(:lat - gul.latitude, 2) + POWER(:lng - gul.longitude, 2)) * 111.325,
                CASE g.status
                WHEN 'ACTIVE' THEN 0 ELSE 1 END,
                GREATEST(ts_rank_cd(to_tsvector('english', COALESCE(gi.name, '')), plainto_tsquery(:q)),
                ts_rank_cd(to_tsvector('english', COALESCE(gi.emoji, '')), plainto_tsquery(:q)),
                ts_rank_cd(to_tsvector('english', COALESCE(gic.name, '')), plainto_tsquery(:q)),
                ts_rank_cd(to_tsvector('english', COALESCE(gu.first_name, '')), plainto_tsquery(:q)),
                ts_rank_cd(to_tsvector('english', COALESCE(gu.last_name, '')), plainto_tsquery(:q))) DESC
            """,
            nativeQuery = true
    )
    Page<GoActivity> search(String q, Double lat, Double lng, Double rad, Pageable pageable, List<Long> ids);
}