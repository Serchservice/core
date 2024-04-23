package com.serch.server.repositories.trip;

import com.serch.server.enums.trip.TripConnectionStatus;
import com.serch.server.models.trip.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, String> {
    @Query("""
            select (count(t) > 0) from Trip t
            where (t.status = ?1 or t.inviteStatus = ?1) or (t.status = ?2 or t.inviteStatus = ?2)
            and (t.account = ?3)
            """)
    boolean existsByStatusAndAccount(
            @NonNull TripConnectionStatus status,
            @NonNull TripConnectionStatus inviteStatus,
            @NonNull String id
    );
    @Query("""
            select (count(t) > 0) from Trip t
            where (t.status = ?1 or t.inviteStatus = ?1) or (t.status = ?2 or t.inviteStatus = ?2)
            and (t.provider.id = ?3 or t.invitedProvider.id = ?3)
            """)
    boolean existsByStatusAndProvider(
            @NonNull TripConnectionStatus status,
            @NonNull TripConnectionStatus inviteStatus,
            @NonNull UUID id
    );
    Optional<Trip> findByIdAndAccount(@NonNull String id, @NonNull String account);
    @Query("select t from Trip t where t.id = ?1 and (t.invitedProvider.id = ?2 or t.provider.id = ?2)")
    Optional<Trip> findByIdAndProviderId(@NonNull String trip, @NonNull UUID id);
    @Query("select t from Trip t where t.account = ?1 or (t.invitedProvider.id = ?2 or t.provider.id = ?2)")
    List<Trip> findByAccount(@NonNull String account, @NonNull UUID id);
    @Query("SELECT t from Trip t where (" +
            "t.account = :userId " +
            ") and (" +
            "t.status = :status " +
            " or t.inviteStatus = :status" +
            ")" +
            "order by t.updatedAt desc"
    )
    List<Trip> todayTrips(@Param("userId") String userId, TripConnectionStatus status);
    @Query(
            "SELECT t from Trip t where (t.provider.id = :userId OR t.provider.business.id = :userId) " +
            "and (t.status = :status) order by t.updatedAt desc"
    )
    List<Trip> todayTrips(@Param("userId")UUID userId, TripConnectionStatus status);
    @Query("SELECT t from Trip t where (" +
            "t.invitedProvider.id = :userId " +
            "OR t.invitedProvider.business.id = :userId " +
            ") and (" +
            "t.status = :status " +
            " or t.inviteStatus = :status" +
            ")" +
            "order by t.updatedAt desc"
    )
    List<Trip> todaySharedTrips(@Param("userId") UUID userId, TripConnectionStatus status);
    @Query("SELECT COUNT(t) FROM Trip t WHERE cast(t.provider.id as string ) IN (SELECT cast(r2.referral.id as string) FROM Referral r2 WHERE r2.referredBy.user.id = :referredBy) OR cast(t.provider.business.id as string) IN (SELECT cast(r2.referral.id as string) FROM Referral r2 WHERE r2.referredBy.user.id = :referredBy) OR t.account IN (SELECT cast(r2.referral.id as string) FROM Referral r2 WHERE r2.referredBy.user.id = :referredBy) AND t.status = 'COMPLETED' ")
    int countCompletedTripsOfReferralsOfReferralsByReferredByAndAccount(@Param("referredBy") UUID referredBy);
    @Query("SELECT COUNT(t) FROM Trip t WHERE cast(t.provider.id as string) = :id OR cast(t.provider.business.id as string) = :id OR t.account = :id AND t.status = 'COMPLETED' ")
    int countCompletedTripsForUserAndAccount(@Param("id") String id);

    @Query("select t from Trip t where t.provider.id = ?1 or t.invitedProvider.id = ?1")
    List<Trip> findByProviderId(@NonNull UUID id);
    @Query("select t from Trip t where t.account = ?1")
    List<Trip> findByAccount(@NonNull String account);
}