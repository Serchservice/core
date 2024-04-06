package com.serch.server.enums.trip;

import lombok.Getter;

@Getter
public enum TripConnectionStatus {
    /// Represents a pending status.
    PENDING("Pending"),
    /// Represents an accepted status.
    ACCEPTED("Accepted"),
    /// Represents a completed status.
    COMPLETED("Completed"),
    /// Represents a left status.
    LEFT("Left the trip"),
    /// Represents a trip status.
    ON_TRIP("Trip has started"),
    /// Represents a cancelled status.
    CANCELLED("Cancelled"),
    /// Represents a not accepted status.
    NOT_ACCEPTED("Not Accepted");

    private final String type;
    TripConnectionStatus(String type) {
        this.type = type;
    }
}
