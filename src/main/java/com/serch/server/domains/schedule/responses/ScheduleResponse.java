package com.serch.server.domains.schedule.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.schedule.ScheduleStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleResponse {
    private String id = "";
    private String time = "";
    private String avatar = "";
    private String name = "";
    private String category = "";
    private String image = "";
    private ScheduleStatus status;
    private String reason = "";
    private String label = "";
    private Double rating = 0.0;
    private String snt;

    @JsonProperty("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @JsonProperty("closed_by")
    private String closedBy = "";

    @JsonProperty("closed_at")
    private String closedAt = "";

    @JsonProperty("closed_on_time")
    private Boolean closedOnTime = Boolean.FALSE;
}