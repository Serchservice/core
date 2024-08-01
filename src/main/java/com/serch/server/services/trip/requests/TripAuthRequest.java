package com.serch.server.services.trip.requests;

import lombok.Data;

@Data
public class TripAuthRequest {
    private String code;
    private String trip;
    private String guest;
}