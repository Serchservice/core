package com.serch.server.enums.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountStatus {
    SUSPENDED("Suspended"),
    ACTIVE("Active"),
    DEACTIVATED("Deactivated"),
    BUSINESS_DEACTIVATED("Business Deactivated"),
    DELETED("Deleted"),
    BUSINESS_DELETED("Business Requested Delete"),
    HAS_REPORTED_ISSUES("Has Reported Issues");

    private final String type;
}