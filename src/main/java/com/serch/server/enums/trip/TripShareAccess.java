package com.serch.server.enums.trip;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TripShareAccess {
    GRANTED("Share access granted"),
    DENIED("Share access not granted");

    private final String value;
}