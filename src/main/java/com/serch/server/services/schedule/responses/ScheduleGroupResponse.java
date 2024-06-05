package com.serch.server.services.schedule.responses;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ScheduleGroupResponse {
    private String label;
    private LocalDateTime time;
    private List<ScheduleResponse> schedules = new ArrayList<>();
}
