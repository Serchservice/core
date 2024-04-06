package com.serch.server.enums.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionType {
    TIP2FIX("Tip2Fix"),
    TRIP("Service Trip"),
    WITHDRAW("Withdrawal"),
    SUBSCRIPTION("Subscription"),
    FUNDING("Fund");

    private final String type;
}
