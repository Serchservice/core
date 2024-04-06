package com.serch.server.enums.trip;

import lombok.Getter;

@Getter
public enum TripArrivalStatus {
    /// Represents a pending status.
    PENDING("Getting ready"),
    /// Represents moving status.
    MOVING("On the way"),
    /// Represents arrived status.
    ARRIVED("Arrived");

    private final String type;
    TripArrivalStatus(String type) {
        this.type = type;
    };
}
