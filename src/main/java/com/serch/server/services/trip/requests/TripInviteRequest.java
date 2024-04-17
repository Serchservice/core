package com.serch.server.services.trip.requests;

import lombok.Data;

import java.util.UUID;

@Data
public class TripInviteRequest {
    private String trip;
    private UUID provider;
}
