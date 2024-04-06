package com.serch.server.enums.verified;

import lombok.Getter;

@Getter
public enum VerificationStage {
    STARTED("Started"),
    /// Represents submitted.
    SUBMITTED("Verification Initiated"),
    /// Represents submitted.
    NOT_STARTED("Verification Not Started"),
    /// Represents completed.
    COMPLETED("Verification Completed");

    private final String type;
    VerificationStage(String type) {
        this.type = type;
    }
}