package com.serch.server.services.trip.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.services.trip.responses.MapViewResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MapViewRequest extends MapViewResponse {
    @JsonProperty("is_shared")
    private Boolean isShared;

    private String trip;
}