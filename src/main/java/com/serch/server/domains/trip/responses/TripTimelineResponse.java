package com.serch.server.domains.trip.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.trip.TripConnectionStatus;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class TripTimelineResponse {
    private TripConnectionStatus status;
    private String header;
    private String description;
    private String label;

    @JsonProperty("is_over")
    private Boolean isOver;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    @JsonProperty("updated_at")
    private ZonedDateTime updatedAt;
}