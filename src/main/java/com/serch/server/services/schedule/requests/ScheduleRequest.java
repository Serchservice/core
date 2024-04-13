package com.serch.server.services.schedule.requests;

import lombok.Data;

import java.util.UUID;

@Data
public class ScheduleRequest {
    private UUID provider;
    private String time;
}
