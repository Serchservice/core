package com.serch.server.enums.verified;

import lombok.Getter;

@Getter
public enum OperationType {
    /// Represents none.
    NONE("None"),
    /// Represents business.
    BUSINESS("Business Entity"),
    /// Represents independent.
    INDEPENDENT_CONTRACTOR("Independent Contractor");

    private final String type;
    OperationType(String type) {
      this.type = type;
    }
}