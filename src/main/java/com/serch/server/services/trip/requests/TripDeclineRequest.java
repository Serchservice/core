package com.serch.server.services.trip.requests;

import lombok.Data;

@Data
public class TripDeclineRequest {
    private String trip;
    private String reason;
}
