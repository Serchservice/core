package com.serch.server.bases;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BaseLocation {
    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("longitude")
    private Double longitude;

    @JsonProperty("place")
    private String place;

    public BaseLocation() {}

    public BaseLocation(Double latitude, Double longitude, String place) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.place = place;
    }

    public BaseLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}