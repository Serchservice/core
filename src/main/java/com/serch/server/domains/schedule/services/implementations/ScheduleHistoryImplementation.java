package com.serch.server.domains.schedule.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.schedule.responses.ScheduleGroupResponse;
import com.serch.server.domains.schedule.responses.ScheduleResponse;
import com.serch.server.domains.schedule.services.ScheduleHistoryService;
import com.serch.server.domains.schedule.services.SchedulingService;
import com.serch.server.models.auth.User;
import com.serch.server.models.schedule.Schedule;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.schedule.ScheduleRepository;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleHistoryImplementation implements ScheduleHistoryService {
    private final AuthUtil authUtil;
    private final SchedulingService schedulingService;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Override
    public List<ScheduleResponse> active(UUID id) {
        return active(id, null, null, false);
    }

    @Override
    public List<ScheduleResponse> pending(UUID id) {
        return active(authUtil.getUser().getId(), null, null, true);
    }

    @Override
    public List<ScheduleGroupResponse> schedules(UUID id) {
        return schedules(id, null, null, null, null, null);
    }

    @Override
    public ApiResponse<List<ScheduleResponse>> active(Integer page, Integer size) {
        return new ApiResponse<>(active(authUtil.getUser().getId(), page, size, false));
    }

    private List<ScheduleResponse> active(UUID id, Integer page, Integer size, boolean isRequested) {
        Page<Schedule> schedules = isRequested
                ? scheduleRepository.pending(id, HelperUtil.getPageable(page, size))
                : scheduleRepository.active(id, HelperUtil.getPageable(page, size));

        if(schedules != null && !schedules.isEmpty() && schedules.hasContent()) {
            return schedules.getContent()
                    .stream()
                    .sorted(Comparator.comparing(Schedule::getUpdatedAt))
                    .map(schedule -> schedulingService.response(
                            schedule,
                            schedule.getProvider().getId().equals(id),
                            userRepository.findById(id).map(User::isProfile).orElse(false)
                    ))
                    .toList();
        }

        return new ArrayList<>();
    }

    @Override
    public ApiResponse<List<ScheduleResponse>> requested(Integer page, Integer size) {
        return new ApiResponse<>(active(authUtil.getUser().getId(), page, size, true));
    }

    @Override
    public ApiResponse<List<ScheduleGroupResponse>> history(Integer page, Integer size, String status, String category, LocalDate date) {
        return new ApiResponse<>(schedules(authUtil.getUser().getId(), page, size, status, category, date));
    }

    private List<ScheduleGroupResponse> schedules(UUID id, Integer page, Integer size, String status, String category, LocalDate date) {
        Page<Schedule> schedules = scheduleRepository.schedules(id, status, category, date, HelperUtil.getPageable(page, size));

        if(schedules != null && !schedules.isEmpty() && schedules.hasContent()) {
            List<ScheduleGroupResponse> list = new ArrayList<>(
                    schedules.getContent()
                    .stream()
                    .collect(Collectors.groupingBy(schedule -> schedule.getCreatedAt().toLocalDate()))
                    .entrySet()
                    .stream()
                    .map((data) -> buildGroupResponse(id, data.getKey(), data.getValue()))
                    .toList()
            );

            list.sort(Comparator.comparing(ScheduleGroupResponse::getTime).reversed());
            return list;
        }
        return List.of();
    }

    private ScheduleGroupResponse buildGroupResponse(UUID id, LocalDate date, List<Schedule> scheduleList) {
        ScheduleGroupResponse response = new ScheduleGroupResponse();

        response.setTime(LocalDateTime.of(date, scheduleList.getFirst().getCreatedAt().toLocalTime()));
        response.setLabel(TimeUtil.formatChatLabel(LocalDateTime.of(date, scheduleList.getFirst().getCreatedAt().toLocalTime()), authUtil.getUser().getTimezone()));
        response.setSchedules(scheduleList.stream()
                .sorted(Comparator.comparing(Schedule::getUpdatedAt).reversed())
                .map(schedule -> schedulingService.response(
                        schedule,
                        schedule.getProvider().getId().equals(id),
                        userRepository.findById(id).map(User::isProfile).orElse(false)
                ))
                .toList()
        );

        return response;
    }
}