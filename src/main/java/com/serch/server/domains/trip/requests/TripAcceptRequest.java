package com.serch.server.domains.trip.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TripAcceptRequest {
    private String trip;

    @JsonProperty("quote_id")
    private Long quoteId;
}
