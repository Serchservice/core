package com.serch.server.domains.schedule.requests;

import lombok.Data;

@Data
public class ScheduleDeclineRequest {
    private String reason;
    private String id;
}
