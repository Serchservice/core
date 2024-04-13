package com.serch.server.services.schedule.services;

import com.serch.backend.bases.ApiResponse;
import com.serch.backend.platform.schedule.requests.ScheduleDeclineRequest;
import com.serch.backend.platform.schedule.requests.ScheduleRequest;
import com.serch.backend.platform.schedule.responses.ScheduleResponse;
import com.serch.backend.platform.schedule.responses.ScheduleTimeResponse;

import java.util.List;
import java.util.UUID;

public interface ScheduleService {
    ApiResponse<String> schedule(ScheduleRequest request);
    ApiResponse<String> accept(String id);
    ApiResponse<String> cancel(String id);
    ApiResponse<String> end(String id);
    ApiResponse<String> startTrip(String id);
    ApiResponse<String> decline(ScheduleDeclineRequest request);
    ApiResponse<List<ScheduleResponse>> fetchDailySchedules();
    ApiResponse<List<ScheduleResponse>> fetchSchedules();
    ApiResponse<List<ScheduleResponse>> fetchDailySchedules(String user);
    ApiResponse<List<ScheduleTimeResponse>> scheduleTimes(UUID id);
}
