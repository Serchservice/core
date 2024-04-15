package com.serch.server.services.trip.requests;

import lombok.Data;

@Data
public class OnlineRequest {
    private String city;
    private String state;
    private String country;
    private String place;
    private Double longitude;
    private Double latitude;
}
