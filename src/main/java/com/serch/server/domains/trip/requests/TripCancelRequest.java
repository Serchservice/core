package com.serch.server.domains.trip.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TripCancelRequest {
    private String trip;
    private String guest;
    private String reason;

    @JsonProperty("link_id")
    private String linkId;
}
