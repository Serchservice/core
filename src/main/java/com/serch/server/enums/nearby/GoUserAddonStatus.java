package com.serch.server.enums.nearby;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GoUserAddonStatus {
    ACTIVE("Active", "is currently active"),
    CANCELLED("Cancelled", "has been cancelled"),
    EXPIRED("Expired", "has expired"),
    RENEWAL_DUE("Renewal Due", "is due for renewal");

    private final String type;
    private final String sentence;
}