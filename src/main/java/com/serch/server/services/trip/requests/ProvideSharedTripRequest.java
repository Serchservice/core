package com.serch.server.services.trip.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProvideSharedTripRequest {
    private String guest;
    private OnlineRequest address;

    @JsonProperty("link_id")
    private String linkId;
}
