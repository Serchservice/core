package com.serch.server.enums.verified;

import lombok.Getter;

/**
 * The OperationType enum represents different types of operations in the application.
 * Each enum constant corresponds to a specific type of operation and provides a descriptive type.
 * <p></p>
 * The operation types are:
 * <ul>
 *     <li>{@link OperationType#NONE} - Represents no specific operation type</li>
 *     <li>{@link OperationType#BUSINESS} - Represents a business entity</li>
 *     <li>{@link OperationType#INDEPENDENT_CONTRACTOR} - Represents an independent contractor</li>
 * </ul>
 */
@Getter
public enum OperationType {
    NONE("None"),
    BUSINESS("Business Entity"),
    INDEPENDENT_CONTRACTOR("Independent Contractor");

    private final String type;
    OperationType(String type) {
      this.type = type;
    }
}