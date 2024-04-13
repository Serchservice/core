package com.serch.server.services.schedule.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleResponse {
    private String id;
    private String time;
    private String avatar;
    private String name;
    private String category;

    @JsonProperty("is_time")
    private Boolean isTime;

    @JsonProperty("date_time")
    private LocalDateTime dateTime;
}
