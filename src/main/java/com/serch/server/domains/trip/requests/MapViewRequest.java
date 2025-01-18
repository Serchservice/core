package com.serch.server.domains.trip.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.domains.trip.responses.MapViewResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MapViewRequest extends MapViewResponse {
    @JsonProperty("is_shared")
    private Boolean isShared;

    private String trip;
}