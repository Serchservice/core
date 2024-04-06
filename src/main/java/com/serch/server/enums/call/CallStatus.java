package com.serch.server.enums.call;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CallStatus {
    /// Represents calling status.
    CALLING("Calling"),
    /// Represents ringing status.
    RINGING("Ringing"),
    /// Represents onCall status.
    ON_CALL("On Call"),
    /// Represents closed call status.
    CLOSED("Closed"),
    /// Represents rejected call status.
    DECLINED("Declined"),
    /// Indicates that the user missed the call
    MISSED("Missed");

    private final String type;
}
