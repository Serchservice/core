package com.serch.server.services.transaction.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.schedule.Schedule;

public interface SchedulePayService {
    ApiResponse<Boolean> charge(Schedule schedule);
    void pay();
}
