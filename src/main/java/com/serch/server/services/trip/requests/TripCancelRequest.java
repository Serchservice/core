package com.serch.server.services.trip.requests;

import lombok.Data;

@Data
public class TripCancelRequest {
    private String trip;
    private String reason;
}
