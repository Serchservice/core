package com.serch.server.enums.subscription;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlanStatus {
    ACTIVE("Active"),
    USED("Used"),
    NOT_USED("Not Used"),
    SUSPENDED("Unsubscribed"),
    EXPIRED("Expired");

    private final String type;
}
