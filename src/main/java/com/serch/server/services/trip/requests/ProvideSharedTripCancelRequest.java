package com.serch.server.services.trip.requests;

import lombok.Data;

@Data
public class ProvideSharedTripCancelRequest {
    private String guest;
    private String trip;
    private String reason;
}
