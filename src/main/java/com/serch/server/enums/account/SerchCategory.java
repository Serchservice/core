package com.serch.server.enums.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SerchCategory {
    /// Represents the `MECHANIC` category in Serch.
    MECHANIC("Mechanic"),
    /// Represents the `PLUMBER` category in Serch.
    PLUMBER("Plumber"),
    /// Represents the `ELECTRICIAN` category in Serch.
    ELECTRICIAN("Electrician"),
    /// Represents the `BUSINESS` category in Serch.
    BUSINESS("Business"),
    /// Represents the `CARPENTER` category in Serch.
    CARPENTER("Carpenter"),
    /// Represents the `USER` category in Serch.
    USER("User");

    private final String type;
}