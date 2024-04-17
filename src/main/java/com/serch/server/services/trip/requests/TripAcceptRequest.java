package com.serch.server.services.trip.requests;

import lombok.Data;

@Data
public class TripAcceptRequest {
    private String trip;
    private OnlineRequest address;
}
