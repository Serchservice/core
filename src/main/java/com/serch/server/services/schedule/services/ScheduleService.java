package com.serch.server.services.schedule.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.schedule.requests.ScheduleDeclineRequest;
import com.serch.server.services.schedule.requests.ScheduleRequest;
import com.serch.server.services.schedule.responses.ScheduleResponse;
import com.serch.server.services.schedule.responses.ScheduleTimeResponse;

import java.util.List;
import java.util.UUID;

public interface ScheduleService {
    ApiResponse<String> request(ScheduleRequest request);
    ApiResponse<String> accept(String id);
    ApiResponse<String> cancel(String id);
    ApiResponse<String> close(String id);
    ApiResponse<String> start(String id);
    ApiResponse<String> decline(ScheduleDeclineRequest request);
    ApiResponse<List<ScheduleResponse>> today();
    ApiResponse<List<ScheduleResponse>> schedules();
    ApiResponse<List<ScheduleTimeResponse>> times(UUID id);
}
