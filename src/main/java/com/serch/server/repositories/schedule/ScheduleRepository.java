package com.serch.server.repositories.schedule;

import com.serch.server.models.schedule.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedule, String> {
    List<Schedule> findByProvider_SerchIdAndCreatedAtBetween(
            UUID serchId,
            LocalDateTime localDateTime,
            LocalDateTime localDateTime1
    );
    List<Schedule> findByClosedByAndClosedOnTime(UUID closedBy, Boolean closedOnTime);
    @Query("SELECT s from Schedule s where (s.user.serchId = :userId OR s.provider.serchId = :userId" +
            " or s.provider.business.serchId = :userId)" +
            "and s.createdAt >= CURRENT_DATE order by s.updatedAt desc"
    )
    List<Schedule> today(UUID user);
    @Query("SELECT s from Schedule s where (s.user.serchId = :userId OR s.provider.serchId = :userId" +
            " or s.provider.business.serchId = :userId)" +
            " order by s.updatedAt desc"
    )
    List<Schedule> schedules(UUID user);
}