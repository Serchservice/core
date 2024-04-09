package com.serch.server.enums.transaction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionType {
    TIP2FIX("Tip2Fix"),
    TRIP("Service Trip"),
    WITHDRAW("Withdrawal"),
    SUBSCRIPTION("Subscription"),
    T2F("Tip2Fix"),
    FUNDING("Fund");

    private final String type;
}
