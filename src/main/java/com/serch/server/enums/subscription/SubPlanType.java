package com.serch.server.enums.subscription;

import lombok.Getter;

@Getter
public enum SubPlanType {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    QUARTERLY("Every Three Months");

    private final String type;
    SubPlanType(String type) {
        this.type = type;
    }
}
