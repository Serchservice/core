package com.serch.server.services.trip.requests;

import lombok.Data;

import java.util.UUID;

@Data
public class TripRequest {
    private UUID provider;
    private OnlineRequest address;
}
