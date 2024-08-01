package com.serch.server.enums.trip;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The TripConnectionStatus enum represents different connection statuses of trips in the application.
 * Each enum constant corresponds to a specific connection status and provides a descriptive type.
 * <p></p>
 */
@Getter
@AllArgsConstructor
public enum TripConnectionStatus {
    REQUESTED("Trip requested", 0, "Waiting for confirmation from provider"),
    CANCELLED("Trip cancelled", 1, "Trip cancelled due to some reasons"),
    CONNECTED("Trip connected", 1, "Trip request is connected, proceeding with actions"),
    ON_THE_WAY("Provider on the way", 2, "Provider is coming over to get your work done"),
    ARRIVED("Provider arrived", 3, "Provider has arrived to the given destination"),
    AUTHENTICATED("Provider verified", 4, "Provider is verified and trip is online"),
    ON_TRIP("Trip has started", 5, "Explore other trip options from this moment on"),
    SHARE_ACCESS_GRANTED("Share access granted", 6, "Provider can now share this trip to another provider"),
    SHARE_ACCESS_DENIED("Share access denied", 6, "Provider cannot share this trip to another provider"),
    LEFT("Left the trip", 7, "Provider left the trip"),
    COMPLETED("Trip Completed", 7, "This trip is completed and successful");

    private final String type;
    private final Integer value;
    private final String description;
}