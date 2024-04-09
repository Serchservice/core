package com.serch.server.enums.transaction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionStatus {
    PENDING("Pending"),
    SUCCESSFUL("Successful"),
    FAILED("Failed");

    private final String type;
}
