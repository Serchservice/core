package com.serch.server.services.trip.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.trip.TripConnectionStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TripTimelineResponse {
    private TripConnectionStatus status;
    private String header;
    private String description;
    private String label;

    @JsonProperty("is_over")
    private Boolean isOver;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}