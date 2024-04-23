package com.serch.server.repositories.schedule;

import com.serch.server.enums.schedule.ScheduleStatus;
import com.serch.server.models.schedule.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedule, String> {
    List<Schedule> findByProvider_IdAndCreatedAtBetween(
            UUID serchId,
            LocalDateTime localDateTime,
            LocalDateTime localDateTime1
    );
    List<Schedule> findByClosedByAndClosedOnTime(UUID closedBy, Boolean closedOnTime);
    @Query("SELECT s from Schedule s where (s.user.id = :userId OR s.provider.id = :userId" +
            " or s.provider.business.id = :userId)" +
            "and s.createdAt >= CURRENT_DATE order by s.updatedAt desc"
    )
    List<Schedule> today(UUID user);
    @Query("SELECT s from Schedule s where (s.user.id = :userId OR s.provider.id = :userId" +
            " or s.provider.business.id = :userId)" +
            " order by s.updatedAt desc"
    )
    List<Schedule> schedules(UUID user);
    List<Schedule> findByCreatedAtBetween(LocalDateTime createdAt, LocalDateTime createdAt2);
    List<Schedule> findByStatusAndCreatedAtBefore(@NonNull ScheduleStatus status, @NonNull LocalDateTime createdAt);

    @Query("select s from Schedule s where s.user.id = ?1 or s.provider.id = ?1 or s.user.business.id = ?1 or s.provider.business.id = ?1")
    List<Schedule> findByUser_Id(@NonNull UUID id);
}