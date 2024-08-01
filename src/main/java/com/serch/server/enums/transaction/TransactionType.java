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
 *     <li>{@link TransactionType#TRIP_CHARGE} - Represents a Service Trip transaction</li>
 *     <li>{@link TransactionType#WITHDRAW} - Represents a Withdrawal transaction</li>
 *     <li>{@link TransactionType#FUNDING} - Represents a Fund transaction</li>
 *     <li>{@link TransactionType#SCHEDULE} - Represents a Schedule transaction</li>
 *     <li>{@link TransactionType#TRIP_SHARE} - Represents a guest to account transaction for shared trip percentage</li>
 * </ul>
 */
@Getter
@RequiredArgsConstructor
public enum TransactionType {
    FUNDING("Fund"),
    WITHDRAW("Withdrawal"),
    SCHEDULE("Schedule"),
    TIP2FIX("Tip2Fix"),
    TRIP_SHARE("Service Trip Connection Debit"),
    SHOPPING("Shopping"),
    TRIP_CHARGE("Service Trip Charge Debit");

    private final String type;
}
