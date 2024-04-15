package com.serch.server.services.trip.requests;

import lombok.Data;

@Data
public class OnlineRequest {
    private String place;
    private String country;
    private String state;
    private String city;
    private Double latitude;
    private Double longitude;
}
