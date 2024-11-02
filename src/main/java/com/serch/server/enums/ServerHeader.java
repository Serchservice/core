package com.serch.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServerHeader {
    GUEST_API_KEY("X-Serch-Guest-Api-Key"),
    GUEST_SECRET_KEY("X-Serch-Guest-Secret-Key"),
    DRIVE_API_KEY("X-Serch-Drive-Api-Key"),
    DRIVE_SECRET_KEY("X-Serch-Drive-Secret-Key"),
    GOOGLE_API_KEY("X-Goog-Api-Key"),
    GOOGLE_FIELD_MASK("X-Goog-FieldMask");

    private final String value;
}