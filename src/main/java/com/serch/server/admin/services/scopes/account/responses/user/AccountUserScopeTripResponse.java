package com.serch.server.admin.services.scopes.account.responses.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.trip.TripMode;
import com.serch.server.enums.trip.TripStatus;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class AccountUserScopeTripResponse {
    private String id;
    private TripMode mode;
    private SerchCategory category;
    private TripStatus status;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    @JsonProperty("updated_at")
    private ZonedDateTime updatedAt;
}