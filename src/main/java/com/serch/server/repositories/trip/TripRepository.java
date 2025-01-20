package com.serch.server.repositories.trip;

import com.serch.server.models.trip.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, String> {
    Optional<Trip> findByIdAndAccount(@NonNull String id, @NonNull String account);

    @Query("select t from Trip t where t.id = ?1 and t.provider.id = ?2")
    Optional<Trip> findByIdAndProviderId(@NonNull String trip, @NonNull UUID id);

    @Query("""
        SELECT COUNT(t) > 0 FROM Trip t
        LEFT JOIN t.timelines tt
        LEFT JOIN t.invited ti
        LEFT JOIN ti.timelines tit
        WHERE t.isActive = true
        AND ((t.provider.id = ?1 AND tt.status NOT IN (com.serch.server.enums.trip.TripConnectionStatus.CANCELLED,
        com.serch.server.enums.trip.TripConnectionStatus.COMPLETED, com.serch.server.enums.trip.TripConnectionStatus.LEFT))
        OR (ti.provider.id = ?1 AND tit.status NOT IN (com.serch.server.enums.trip.TripConnectionStatus.CANCELLED,
        com.serch.server.enums.trip.TripConnectionStatus.COMPLETED, com.serch.server.enums.trip.TripConnectionStatus.LEFT)))
    """)
    boolean existsByProviderIdAndTimelineStatus(UUID id);


    @Query("select t from Trip t where t.account = ?1")
    List<Trip> findByAccount(@NonNull String account);

    @Query("SELECT COUNT(t) from Trip t where t.account = ?1 and t.createdAt between ?2 and ?3")
    long countByAccountAndDate(@NonNull String account, ZonedDateTime from, ZonedDateTime to);

    @Query("""
        SELECT COUNT(t) from Trip t
        left join t.invited ti
        left join t.provider p
        left join p.business b
        left join ti.provider tip
        left join tip.business tib
        where ((p.id = ?1 or b.id = ?1) or (tip.id = ?1 or tib.id = ?1)) and t.createdAt between ?2 and ?3
    """)
    long countByAccountAndDate(@NonNull UUID account, ZonedDateTime from, ZonedDateTime to);

    @Query("select t from Trip t where t.account = ?1 and t.status = 'WAITING'")
    Page<Trip> findByRequestedUserTrips(@NonNull String account, Pageable pageable);

    @Query("select t from Trip t where t.account = ?1 and t.linkId = ?2 and t.status = 'WAITING'")
    Page<Trip> findByRequestedGuestTrips(@NonNull String account, @NonNull String linkId, Pageable pageable);

    @Query("""
        select t from Trip t
        left join t.invited ti
        left join t.provider p
        left join p.business b
        left join ti.provider tip
        left join tip.business tib
        where ((p.id = ?1 or b.id = ?1) or (tip.id = ?1 or tib.id = ?1)) and t.status = 'WAITING'
    """)
    Page<Trip> findByRequestedProviderTrips(@NonNull UUID id, Pageable pageable);

    @Query("""
        select t from Trip t
        left join t.invited i
        where t.account = :account
        and t.status = 'ACTIVE'
        and (i is null or (t.access = 'GRANTED' and i.status = 'ACTIVE'))
    """)
    Page<Trip> findByActiveUserTrips(@NonNull String account, Pageable pageable);

    @Query("""
        select t from Trip t
        left join t.invited i
        where t.account = ?1 and t.linkId = ?2
        and t.status = 'ACTIVE'
        and (i is null or (t.access = 'GRANTED' and i.status = 'ACTIVE'))
    """)
    Page<Trip> findByActiveGuestTrips(@NonNull String account, @NonNull String linkId, Pageable pageable);

    @Query("""
        select t from Trip t
        left join t.invited ti
        left join t.provider p
        left join p.business b
        left join ti.provider tip
        left join tip.business tib
        where ((p.id = ?1 or b.id = ?1) or (tip.id = ?1 or tib.id = ?1))
        and t.status = 'ACTIVE'
        and (ti is null or (t.access = 'GRANTED' and ti.status = 'ACTIVE'))
    """)
    Page<Trip> findByActiveProviderTrips(@NonNull UUID id, Pageable pageable);

    @Query("""
        select t from Trip t
        left join t.invited ti
        where t.account = :account
        and (t.status = 'UNFULFILLED' or t.status = 'CLOSED')
        and (:category is null or t.category = :category)
        and (:date is null or CAST(t.createdAt AS DATE) = :date)
        and (:isShared is null or (ti is not null and :isShared = true))
    """)
    Page<Trip> findByUserHistoryTrips(@Param("account") String account, @Param("category") String category, @Param("date") LocalDate date, @Param("isShared") Boolean isShared, Pageable pageable);

    @Query("""
        select t from Trip t
        left join t.invited ti
        where t.account = :account and t.linkId = :linkId
        and (t.status = 'UNFULFILLED' or t.status = 'CLOSED')
        and (:category is null or t.category = :category)
        and (:date is null or CAST(t.createdAt AS DATE) = :date)
        and (:isShared is null or (ti is not null and :isShared = true))
    """)
    Page<Trip> findByGuestHistoryTrips(@Param("account") String account, @Param("linkId") String linkId, @Param("category") String category, @Param("date") LocalDate date, @Param("isShared") Boolean isShared, Pageable pageable);

    @Query("""
        select t from Trip t
        left join t.invited ti
        left join t.provider p
        left join t.provider.business pb
        left join ti.provider inp
        left join ti.provider.business inpb
        where ((p.id = :userId or pb.id = :userId) or (inp.id = :userId or inpb.id = :userId))
        and (t.status = 'UNFULFILLED' or t.status = 'CLOSED')
        and (:category is null or t.category = :category)
        and (:date is null or CAST(t.createdAt AS DATE) = :date)
        and (:isShared is null or (ti is not null and :isShared = true))
    """)
    Page<Trip> findByProviderHistoryTrips(@Param("userId") UUID id, @Param("category") String category, @Param("date") LocalDate date, @Param("isShared") Boolean isShared, Pageable pageable);

    @Query("""
        select t from Trip t
        left join t.invited ti
        where t.account = ?1
        and t.status = 'ACTIVE'
        and ti is not null
        and t.access = 'GRANTED'
        and ti.status = 'WAITING'
    """)
    Page<Trip> findBySharedUserTrips(@NonNull String account, Pageable pageable);

    @Query("""
        select t from Trip t
        left join t.invited ti
        where t.account = ?1 and t.linkId = ?2
        and t.status = 'ACTIVE'
        and ti is not null
        and t.access = 'GRANTED'
        and ti.status = 'WAITING'
    """)
    Page<Trip> findBySharedGuestTrips(@NonNull String account, @NonNull String linkId, Pageable pageable);

    @Query("""
        select t from Trip t
        left join t.invited ti
        left join t.provider p
        left join p.business b
        left join ti.provider tip
        left join tip.business tib
        where ((p.id = ?1 or b.id = ?1) or (tip.id = ?1 or tib.id = ?1))
        and t.status = 'ACTIVE'
        and ti is not null
        and t.access = 'GRANTED'
        and ti.status = 'WAITING'
    """)
    Page<Trip> findBySharedProviderTrips(@NonNull UUID id, Pageable pageable);

    @Query("select t from Trip t where t.account = ?1")
    Page<Trip> findByAccount(@NonNull String account, Pageable pageable);

    @Query("select t from Trip t where t.account = ?1 and t.linkId = ?2")
    Page<Trip> findByAccount(@NonNull String account, @NonNull String linkId, Pageable pageable);

    @Query("""
        select t from Trip t
        left join t.timelines tt
        where t.account = ?1
        and tt.status in (com.serch.server.enums.trip.TripConnectionStatus.COMPLETED)
        and FUNCTION('DATE', t.updatedAt) = FUNCTION('CURRENT_DATE')
        order by t.updatedAt desc
    """)
    List<Trip> todayTrips(String id);

    @Query("""
        select t from Trip t
        left join t.timelines tt
        where (t.provider.id = ?1 or t.provider.business.id = ?1)
        and tt.status in (com.serch.server.enums.trip.TripConnectionStatus.COMPLETED)
        and FUNCTION('DATE', t.updatedAt) = FUNCTION('CURRENT_DATE')
        order by t.updatedAt desc
    """)
    List<Trip> todayTrips(UUID id);

    @Query("""
        select t from Trip t
        left join t.timelines tt
        where (t.provider.id = ?1 or t.provider.business.id = ?1)
        and tt.status in (com.serch.server.enums.trip.TripConnectionStatus.COMPLETED)
        order by t.updatedAt desc
    """)
    List<Trip> findCompletedProviderTrips(UUID id);

    @Query("""
        select t from Trip t
        left join t.invited ti
        left join t.provider p
        left join p.business b
        left join ti.provider tip
        left join tip.business tib
        where (p.id = ?1 or b.id = ?1)
        or (tip.id = ?1 or tib.id = ?1)
    """)
    List<Trip> findByProviderId(@NonNull UUID id);

    @Query("""
        select t from Trip t
        left join t.invited ti
        left join t.provider p
        left join p.business b
        left join ti.provider tip
        left join tip.business tib
        where (p.id = ?1 or b.id = ?1)
        or (tip.id = ?1 or tib.id = ?1)
    """)
    Page<Trip> findByProviderId(@NonNull UUID id, Pageable pageable);

    @Query("""
        select t from Trip t
        left join t.provider p
        group by t.id, t.cancelReason, t.invited, t.account, t.access, t.isActive,
                 t.address, t.amount, t.userShare, t.authentication, t.address,
                 t.shared, t.serviceFee, t.provider
        order by count(p.category) desc
    """)
    List<Trip> findPopularCategories(Pageable pageable);

    @Query("""
        select count(t) > 0 from Trip t
        left join t.timelines tt
        where t.account = ?1
        and tt.status not in (
            com.serch.server.enums.trip.TripConnectionStatus.CANCELLED,
            com.serch.server.enums.trip.TripConnectionStatus.COMPLETED,
            com.serch.server.enums.trip.TripConnectionStatus.LEFT
        )
        and t.isActive = true
    """)
    boolean existsByStatusAndAccount(String s);

    List<Trip> findByProvider_Id(@NonNull UUID id);
}