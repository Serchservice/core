package com.serch.server.enums.trip;

import lombok.Getter;

/**
 * The TripStatus enum represents different statuses of trips in the application.
 * Each enum constant corresponds to a specific status and provides a descriptive type.
 * <p></p>
 * The trip statuses are:
 * <ul>
 *     <li>{@link TripStatus#ONLINE} - Represents a provider that is available online</li>
 *     <li>{@link TripStatus#OFFLINE} - Represents a provider that is unavailable online</li>
 *     <li>{@link TripStatus#BUSY} - Represents a provider that is available online but busy</li>
 *     <li>{@link TripStatus#NONE} - Represents a provider that is searching for availability</li>
 *     <li>{@link TripStatus#REQUESTSHARING} - Represents a provider that is available online but with request sharing</li>
 * </ul>
 */
@Getter
public enum TripStatus {
    ONLINE("Online"),
    OFFLINE("Offline"),
    BUSY("Online but Busy"),
    NONE("Searching"),
    REQUESTSHARING("Online but RequestSharing");

    private final String type;
    TripStatus(String type) {
        this.type = type;
    };
}