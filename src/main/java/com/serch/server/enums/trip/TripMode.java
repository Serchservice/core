package com.serch.server.enums.trip;

import lombok.Getter;

/**
 * The TripMode enum represents different modes of trips in the application.
 * Each enum constant corresponds to a specific mode and provides a descriptive type.
 * <p></p>
 * The trip modes are:
 * <ul>
 *     <li>{@link TripMode#FROM_GUEST} - Represents a trip initiated from a guest</li>
 *     <li>{@link TripMode#FROM_USER} - Represents a trip initiated from a registered user</li>
 * </ul>
 */
@Getter
public enum TripMode {
    FROM_GUEST("From Guest"),
    FROM_USER("From User");

    private final String type;
    TripMode(String type) {
        this.type = type;
    };
}