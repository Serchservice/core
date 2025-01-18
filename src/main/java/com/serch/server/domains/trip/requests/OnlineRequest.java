package com.serch.server.domains.trip.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OnlineRequest {
    @JsonProperty("place_id")
    private String placeId;

    private String address;
    private Double latitude;
    private Double longitude;
}