package com.serch.server.services.trip.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TripAcceptRequest {
    private String trip;
    private String guest;

    @JsonProperty("quote_id")
    private Long quoteId;
}
