package com.serch.server.enums.trip;

import lombok.Getter;

/**
 * The TripConnectionStatus enum represents different connection statuses of trips in the application.
 * Each enum constant corresponds to a specific connection status and provides a descriptive type.
 * <p></p>
 * The connection statuses are:
 * <ul>
 *     <li>{@link TripConnectionStatus#PENDING} - Represents the connection is pending for the trip</li>
 *     <li>{@link TripConnectionStatus#ACCEPTED} - Represents the connection is accepted for the trip</li>
 *     <li>{@link TripConnectionStatus#COMPLETED} - Represents the trip connection is completed</li>
 *     <li>{@link TripConnectionStatus#LEFT} - Represents the user left the trip</li>
 *     <li>{@link TripConnectionStatus#ON_TRIP} - Represents the trip has started</li>
 *     <li>{@link TripConnectionStatus#CANCELLED} - Represents the trip connection is cancelled</li>
 *     <li>{@link TripConnectionStatus#NOT_ACCEPTED} - Represents the trip connection is not accepted</li>
 * </ul>
 */
@Getter
public enum TripConnectionStatus {
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    COMPLETED("Completed"),
    LEFT("Left the trip"),
    ON_TRIP("Trip has started"),
    CANCELLED("Cancelled"),
    NOT_ACCEPTED("Not Accepted");

    private final String type;
    TripConnectionStatus(String type) {
        this.type = type;
    }
}
