package com.serch.server.services.trip.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.trip.TripConnectionStatus;
import lombok.Data;

@Data
public class TripUpdateRequest {
    private String trip;
    private String guest;
    private TripConnectionStatus status;

    @JsonProperty("is_shared")
    private Boolean isShared = false;
}