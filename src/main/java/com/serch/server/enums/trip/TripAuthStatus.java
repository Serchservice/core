package com.serch.server.enums.trip;

import lombok.Getter;

@Getter
public enum TripAuthStatus {
    /// Represents a pending status.
    NONE("None"),
    /// Represents moving status.
    REQUESTED("Waiting"),
    /// Represents arrived status.
    VERIFIED("Verified"),
    /// Represents arrived status.
    NOT_VERIFIED("Not Verified");

    private final String type;
    TripAuthStatus(String type) {
        this.type = type;
    };
}
