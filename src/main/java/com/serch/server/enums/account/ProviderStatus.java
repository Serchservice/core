package com.serch.server.enums.account;

import lombok.Getter;

/**
 * The TripStatus enum represents different statuses of trips in the application.
 * Each enum constant corresponds to a specific status and provides a descriptive type.
 * <p></p>
 * The trip statuses are:
 * <ul>
 *     <li>{@link ProviderStatus#ONLINE} - Represents a provider that is available online</li>
 *     <li>{@link ProviderStatus#OFFLINE} - Represents a provider that is unavailable online</li>
 *     <li>{@link ProviderStatus#BUSY} - Represents a provider that is available online but busy</li>
 *     <li>{@link ProviderStatus#NONE} - Represents a provider that is searching for availability</li>
 *     <li>{@link ProviderStatus#REQUESTSHARING} - Represents a provider that is available online but with request sharing</li>
 * </ul>
 */
@Getter
public enum ProviderStatus {
    ONLINE("Online"),
    OFFLINE("Offline"),
    BUSY("Online but Busy"),
    NONE("Searching"),
    REQUESTSHARING("Online but RequestSharing");

    private final String type;
    ProviderStatus(String type) {
        this.type = type;
    }
}