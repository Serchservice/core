package com.serch.server.enums.trip;

import lombok.Getter;

@Getter
public enum TripMode {
    /// This represents the `From Guest` ServiceTripMode in the Serch app.
    FROM_GUEST("From Guest"),

    /// This represents the `From User` ServiceTripMode in the Serch app.
    FROM_USER("From User");

    private final String type;
    TripMode(String type) {
        this.type = type;
    };
}