package com.serch.server.enums.nearby;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GoAddonPlanInterval {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    QUARTERLY("Quarterly"),
    ONCE("One-Time"),
    YEARLY("Yearly");

    private final String value;
}