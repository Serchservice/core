package com.serch.server.enums.shared;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UseStatus {
    /// This represents the `USED` status in the Serch app.
    USED("Used"),
    /// This represents the `COUNT1` status in the Serch app.
    COUNT_1("Count1"),
    /// This represents the `COUNT2` status in the Serch app.
    COUNT_2("Count2"),
    /// This represents the `COUNT3` status in the Serch app.
    COUNT_3("Count3"),
    /// This represents the `COUNT4` status in the Serch app.
    COUNT_4("Count4"),
    /// This represents the `NOT USED` status in the Serch app.
    NOT_USED("Not Used");

    private final String type;
}