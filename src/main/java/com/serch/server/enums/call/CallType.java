package com.serch.server.enums.call;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CallType {
    /// Represents a voice call sub.
    VOICE("Voice"),
    /// Represents a tip-to-fix (Tip2Fix) call sub.
    T2F("T2F"),
    /// Represents a none call sub.
    NONE("None");

    private final String type;
}
