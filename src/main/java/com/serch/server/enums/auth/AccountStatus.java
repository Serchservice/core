package com.serch.server.enums.auth;

import lombok.Getter;

@Getter
public enum AccountStatus {
    /// This represents the `SUSPENDED` status of the account.
    SUSPENDED("Suspended"),
    /// This represents the `ACTIVE` status of the account.
    ACTIVE("Active"),
    /// This represents the `DEACTIVATED` status of the account.
    DEACTIVATED("Deactivated"),
    /// This represents the `BUSINESS_DEACTIVATED` status of the account.
    BUSINESS_DEACTIVATED("Business Deactivated"),
    /// This represents the `HAS REPORTED ISSUES` status of the account.
    HAS_REPORTED_ISSUES("Has Reported Issues");

    private final String type;
    AccountStatus(String type) {
        this.type = type;
    }
}