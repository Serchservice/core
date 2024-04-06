package com.serch.server.enums.trip;

import lombok.Getter;

@Getter
public enum TripStatus {
    /// To tell if the provider is online/active
    ONLINE("Online"),
    /// To tell if the provider is offline/not-active
    OFFLINE("Offline"),
    /// To tell if the provider is online but busy
    BUSY("Online but Busy"),
    /// To tell if a search is being made on the provider
    NONE("Searching"),
    /// To tell if the provider is online but requestSharing
    REQUESTSHARING("Online but RequestSharing");

    private final String type;
    TripStatus(String type) {
        this.type = type;
    };
}