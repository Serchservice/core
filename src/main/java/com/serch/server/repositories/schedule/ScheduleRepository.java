package com.serch.server.repositories.schedule;

import com.serch.server.enums.schedule.ScheduleStatus;
import com.serch.server.models.schedule.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedule, String> {
    List<Schedule> findByProvider_IdAndCreatedAtBetween(UUID serchId, ZonedDateTime start, ZonedDateTime end);

    List<Schedule> findByClosedByAndClosedOnTime(UUID closedBy, Boolean closedOnTime);

    @Query("SELECT s from Schedule s where (s.user.id = ?1 OR s.provider.id = ?1 or s.provider.business.id = ?1) and (s.status = 'PENDING' or s.status = 'ACCEPTED') order by s.updatedAt desc"
    )
    List<Schedule> active(UUID userId);

    @Query("SELECT s from Schedule s where (s.user.id = ?1 OR s.provider.id = ?1 or s.provider.business.id = ?1) and s.status = 'ACCEPTED' order by s.updatedAt desc"
    )
    Page<Schedule> active(UUID userId, Pageable pageable);

    @Query("SELECT s from Schedule s where (s.user.id = ?1 OR s.provider.id = ?1 or s.provider.business.id = ?1) and s.status = 'PENDING' order by s.updatedAt desc")
    Page<Schedule> pending(UUID userId, Pageable pageable);

    @Query("""
        SELECT s from Schedule s where (s.user.id = :userId OR s.provider.id = :userId or s.provider.business.id = :userId)
        and s.status != 'PENDING' and s.status != 'ACCEPTED'
        and (:category is null or s.provider.category = :category)
        and (:date is null or function('DATE', s.createdAt) = :date)
        and (:status is null or s.status = :status)
        order by s.updatedAt desc
    """)
    Page<Schedule> schedules(@Param("userId") UUID userId, @Param("status") String status, @Param("category") String category, @Param("date") Date date, Pageable pageable);

    List<Schedule> findByCreatedAtBetween(ZonedDateTime createdAt, ZonedDateTime createdAt2);

    List<Schedule> findByStatusAndCreatedAtBefore(@NonNull ScheduleStatus status, @NonNull ZonedDateTime createdAt);

    @Query("select s from Schedule s where s.user.id = ?1 or s.provider.id = ?1 or s.user.business.id = ?1 or s.provider.business.id = ?1")
    List<Schedule> findByUserId(@NonNull UUID id);

    @Query("select s from Schedule s where s.user.id = ?1 or s.provider.id = ?1 or s.user.business.id = ?1 or s.provider.business.id = ?1")
    Page<Schedule> findByUserId(@NonNull UUID id, Pageable pageable);

    @Query("select count(s) from Schedule s where (s.user.id = ?1 or s.provider.id = ?1 or s.user.business.id = ?1 or s.provider.business.id = ?1) and s.createdAt between ?2 and ?3")
    long countByIdAndDate(@NonNull UUID id, ZonedDateTime from, ZonedDateTime to);

    @Query("select s from Schedule s where (s.user.id = ?1 and s.provider.id = ?2) and (s.status = 'PENDING' or s.status = 'ACCEPTED')")
    Optional<Schedule> findByUserAndProvider(@NonNull UUID id, @NonNull UUID id1);
}