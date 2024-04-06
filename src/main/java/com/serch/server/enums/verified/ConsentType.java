package com.serch.server.enums.verified;

import lombok.Getter;

@Getter
public enum ConsentType {
    /// Represents none.
    NONE("None"),
    /// Represents yes.
    YES("Yes"),
    /// Represents no.
    NO("No");

    private final String type;

    ConsentType(String type) {
        this.type = type;
    }
}
