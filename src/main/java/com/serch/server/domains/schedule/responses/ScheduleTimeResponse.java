package com.serch.server.domains.schedule.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ScheduleTimeResponse {
    private String time;

    @JsonProperty("am_taken")
    private Boolean isAmTaken;

    @JsonProperty("pm_taken")
    private Boolean isPmTaken;
}