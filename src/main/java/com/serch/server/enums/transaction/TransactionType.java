package com.serch.server.enums.transaction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The TransactionType enum represents different types of transactions in the application.
 * Each enum constant corresponds to a specific transaction type and provides a descriptive type.
 * <p></p>
 * The transaction types are:
 * <ul>
 *     <li>{@link TransactionType#TIP2FIX} - Represents a Tip2Fix transaction</li>
 *     <li>{@link TransactionType#TRIP} - Represents a Service Trip transaction</li>
 *     <li>{@link TransactionType#WITHDRAW} - Represents a Withdrawal transaction</li>
 *     <li>{@link TransactionType#SUBSCRIPTION} - Represents a Subscription transaction</li>
 *     <li>{@link TransactionType#T2F} - Represents a Tip2Fix transaction</li>
 *     <li>{@link TransactionType#FUNDING} - Represents a Fund transaction</li>
 *     <li>{@link TransactionType#TRIP_WITHDRAW} - Represents a trip withdrawal transaction</li>
 * </ul>
 */
@Getter
@RequiredArgsConstructor
public enum TransactionType {
    TIP2FIX("Tip2Fix"),
    TRIP("Service Trip"),
    WITHDRAW("Withdrawal"),
    SUBSCRIPTION("Subscription"),
    T2F("Tip2Fix"),
    FUNDING("Fund"),
    TRIP_WITHDRAW("Trip Withdrawal");

    private final String type;
}
