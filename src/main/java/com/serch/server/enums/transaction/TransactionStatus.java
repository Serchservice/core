package com.serch.server.enums.transaction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The TransactionStatus enum represents different statuses of transactions in the application.
 * Each enum constant corresponds to a specific transaction status and provides a descriptive type.
 * <p></p>
 * The transaction statuses are:
 * <ul>
 *     <li>{@link TransactionStatus#PENDING} - Represents a pending transaction</li>
 *     <li>{@link TransactionStatus#SUCCESSFUL} - Represents a successful transaction</li>
 *     <li>{@link TransactionStatus#FAILED} - Represents a failed transaction</li>
 * </ul>
 */
@Getter
@RequiredArgsConstructor
public enum TransactionStatus {
    PENDING("Pending"),
    SUCCESSFUL("Successful"),
    FAILED("Failed");

    private final String type;
}
