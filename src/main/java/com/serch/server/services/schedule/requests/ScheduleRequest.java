package com.serch.server.services.schedule.requests;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ScheduleRequest {
    private UUID provider;
    private String time;
}
