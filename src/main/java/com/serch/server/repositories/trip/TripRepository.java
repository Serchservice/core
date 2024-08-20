package com.serch.server.repositories.trip;

import com.serch.server.models.trip.Trip;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, String> {
    Optional<Trip> findByIdAndAccount(@NonNull String id, @NonNull String account);

    @Query("select t from Trip t where t.id = ?1 and t.provider.id = ?2")
    Optional<Trip> findByIdAndProviderId(@NonNull String trip, @NonNull UUID id);

    @Query("SELECT COUNT(t) > 0 FROM Trip t " +
            "LEFT JOIN t.timelines tt " +
            "LEFT JOIN t.invited ti " +
            "LEFT JOIN ti.timelines tit " +
            "WHERE t.isActive = true " +
            "AND ((t.provider.id = ?1 AND tt.status NOT IN (com.serch.server.enums.trip.TripConnectionStatus.CANCELLED, " +
            "com.serch.server.enums.trip.TripConnectionStatus.COMPLETED, com.serch.server.enums.trip.TripConnectionStatus.LEFT)) " +
            "OR (ti.provider.id = ?1 AND tit.status NOT IN (com.serch.server.enums.trip.TripConnectionStatus.CANCELLED, " +
            "com.serch.server.enums.trip.TripConnectionStatus.COMPLETED, com.serch.server.enums.trip.TripConnectionStatus.LEFT)))"
    )
    boolean existsByProviderIdAndTimelineStatus(UUID id);


    @Query("select t from Trip t where t.account = ?1")
    List<Trip> findByAccount(@NonNull String account);

    @Query("select t from Trip t where t.account = ?1 and t.linkId = ?2")
    List<Trip> findByAccount(@NonNull String account, @NonNull String linkId);

    @Query("SELECT t from Trip t left JOIN t.timelines tt where t.account = ?1 and " +
            "tt.status in (com.serch.server.enums.trip.TripConnectionStatus.COMPLETED) " +
            "AND FUNCTION('DATE',t.updatedAt) = FUNCTION('CURRENT_DATE') order by t.updatedAt desc"
    )
    List<Trip> todayTrips(String id);

    @Query(
            "SELECT t from Trip t left JOIN t.timelines tt where (t.provider.id = ?1 OR t.provider.business.id = ?1) and " +
                    "tt.status in (com.serch.server.enums.trip.TripConnectionStatus.COMPLETED) " +
            "AND FUNCTION('DATE',t.updatedAt) = FUNCTION('CURRENT_DATE') order by t.updatedAt desc"
    )
    List<Trip> todayTrips(UUID id);

    @Query("select t from Trip t " +
            "left join t.invited ti " +
            "left join t.provider p " +
            "left join p.business b " +
            "left join ti.provider tip " +
            "left join tip.business tib " +
            "where (p.id = ?1 or b.id = ?1) " +
            "or (tip.id = ?1 or tib.id = ?1)")
    List<Trip> findByProviderId(@NonNull UUID id);

    @Query("SELECT t FROM Trip t left JOIN t.provider p GROUP BY t.id, t.cancelReason, t.invited, t.account, t.access, t.isActive, t.address, t.amount, t.userShare, t.authentication, t.address, t.shared, t.serviceFee, t.provider ORDER BY COUNT(p.category) DESC")
    List<Trip> findPopularCategories(Pageable pageable);

    @Query("SELECT COUNT(t) > 0 FROM Trip t left JOIN t.timelines tt WHERE t.account = ?1 " +
            "AND tt.status NOT IN (com.serch.server.enums.trip.TripConnectionStatus.CANCELLED, " +
            "com.serch.server.enums.trip.TripConnectionStatus.COMPLETED, " +
            "com.serch.server.enums.trip.TripConnectionStatus.LEFT) AND t.isActive = true")
    boolean existsByStatusAndAccount(String s);

    List<Trip> findByProvider_Id(@NonNull UUID id);
}