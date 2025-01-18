package com.serch.server.domains.trip.responses;

import lombok.Data;

@Data
public class MapViewResponse {
    private Double latitude;
    private Double longitude;
    private Double bearing = 0.0;
    private String place;
}