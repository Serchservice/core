package com.serch.server.repositories.schedule;

import com.serch.server.enums.schedule.ScheduleStatus;
import com.serch.server.models.schedule.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedule, String> {
    List<Schedule> findByProvider_IdAndCreatedAtBetween(UUID serchId, ZonedDateTime start, ZonedDateTime end);

    List<Schedule> findByClosedByAndClosedOnTime(UUID closedBy, Boolean closedOnTime);
    @Query("SELECT s from Schedule s where (s.user.id = ?1 OR s.provider.id = ?1 or s.provider.business.id = ?1) " +
            "and (s.status = 'PENDING' or s.status = 'ACCEPTED') order by s.updatedAt desc"
    )
    List<Schedule> active(UUID userId);

    @Query("SELECT s from Schedule s where (s.user.id = ?1 OR s.provider.id = ?1 or s.provider.business.id = ?1) " +
            "and s.status != 'PENDING' and s.status != 'ACCEPTED' order by s.updatedAt desc"
    )
    List<Schedule> schedules(UUID userId);

    List<Schedule> findByCreatedAtBetween(ZonedDateTime createdAt, ZonedDateTime createdAt2);

    List<Schedule> findByStatusAndCreatedAtBefore(@NonNull ScheduleStatus status, @NonNull ZonedDateTime createdAt);

    @Query("select s from Schedule s where s.user.id = ?1 or s.provider.id = ?1 or s.user.business.id = ?1 or s.provider.business.id = ?1")
    List<Schedule> findByUser_Id(@NonNull UUID id);

    @Query("select s from Schedule s where (s.user.id = ?1 and s.provider.id = ?2) and (s.status = 'PENDING' or s.status = 'ACCEPTED')")
    Optional<Schedule> findByUserAndProvider(@NonNull UUID id, @NonNull UUID id1);
}