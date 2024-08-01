package com.serch.server.enums.trip;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TripStatus {
    ACTIVE("Active"),
    CLOSED("Closed"),
    UNFULFILLED("Unfulfilled"),
    WAITING("Waiting");

    private final String value;
}
