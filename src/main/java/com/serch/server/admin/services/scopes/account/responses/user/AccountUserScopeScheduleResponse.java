package com.serch.server.admin.services.scopes.account.responses.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.schedule.ScheduleStatus;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class AccountUserScopeScheduleResponse {
    private String address;
    private Double latitude;
    private Double longitude;
    private ScheduleStatus status;
    private String reason;
    private String id;
    private String time;

    @JsonProperty("closed_by")
    private UUID closedBy;

    @JsonProperty("closed_at")
    private String closedAt;

    @JsonProperty("closed_on_time")
    private Boolean closedOnTime = Boolean.FALSE;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    @JsonProperty("updated_at")
    private ZonedDateTime updatedAt;
}
