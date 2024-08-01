package com.serch.server.enums.trip;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TripType {
    REQUEST("Request"),
    SPEAK_TO("Speak to"),
    DRIVE_TO("Drive to");

    private final String value;
}