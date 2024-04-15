package com.serch.server.services.map.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Represents a place with location information.
 */
@Data
public class Place {
    @JsonProperty("place_id")
    private String placeId;

    @JsonProperty("long_name")
    private String longName;

    @JsonProperty("short_name")
    private String shortName;

    private String address;
    private Double latitude;
    private Double longitude;
}