package com.serch.server.enums.trip;

import lombok.Getter;

/**
 * The TripAuthStatus enum represents different authentication statuses of trips in the application.
 * Each enum constant corresponds to a specific authentication status and provides a descriptive type.
 * <p></p>
 * The authentication statuses are:
 * <ul>
 *     <li>{@link TripAuthStatus#NONE} - Represents no authentication required for the trip</li>
 *     <li>{@link TripAuthStatus#REQUESTED} - Represents authentication is requested for the trip</li>
 *     <li>{@link TripAuthStatus#VERIFIED} - Represents the authentication is verified for the trip</li>
 *     <li>{@link TripAuthStatus#NOT_VERIFIED} - Represents the authentication is not verified for the trip</li>
 * </ul>
 */
@Getter
public enum TripAuthStatus {
    NONE("None"),
    REQUESTED("Waiting"),
    VERIFIED("Verified"),
    NOT_VERIFIED("Not Verified");

    private final String type;
    TripAuthStatus(String type) {
        this.type = type;
    };
}
