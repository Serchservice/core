package com.serch.server.enums.trip;

import lombok.Getter;

/**
 * The TripArrivalStatus enum represents different arrival statuses of trips in the application.
 * Each enum constant corresponds to a specific arrival status and provides a descriptive type.
 * <p></p>
 * The arrival statuses are:
 * <ul>
 *     <li>{@link TripArrivalStatus#PENDING} - Represents the trip is getting ready</li>
 *     <li>{@link TripArrivalStatus#MOVING} - Represents the trip is on the way</li>
 *     <li>{@link TripArrivalStatus#ARRIVED} - Represents the trip has arrived</li>
 * </ul>
 */
@Getter
public enum TripArrivalStatus {
    PENDING("Getting ready"),
    MOVING("On the way"),
    ARRIVED("Arrived");

    private final String type;
    TripArrivalStatus(String type) {
        this.type = type;
    };
}
