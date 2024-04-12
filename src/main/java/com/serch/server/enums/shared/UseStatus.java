package com.serch.server.enums.shared;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UseStatus {
    /// This represents the `USED` status in the Serch app.
    USED("Used", 5),
    /// This represents the `COUNT1` status in the Serch app.
    COUNT_1("Count1", 1),
    /// This represents the `COUNT2` status in the Serch app.
    COUNT_2("Count2", 2),
    /// This represents the `COUNT3` status in the Serch app.
    COUNT_3("Count3", 3),
    /// This represents the `COUNT4` status in the Serch app.
    COUNT_4("Count4", 4),
    /// This represents the `NOT USED` status in the Serch app.
    NOT_USED("Not Used", 0);

    private final String type;
    private final Integer count;
}