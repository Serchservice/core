package com.serch.server.enums.shop;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The ShopStatus enum represents different statuses of a shop in the application.
 * Each enum constant corresponds to a specific shop status and provides a descriptive type.
 * <p></p>
 * The shop statuses are:
 * <ul>
 *     <li>{@link ShopStatus#OPEN} - Represents an open shop with online operations</li>
 *     <li>{@link ShopStatus#SUSPENDED} - Represents a suspended shop with offline operations</li>
 *     <li>{@link ShopStatus#CLOSED} - Represents a closed shop with offline operations</li>
 * </ul>
 */
@Getter
@RequiredArgsConstructor
public enum ShopStatus {
    OPEN("Open - Online"),
    SUSPENDED("Suspended - Offline"),
    CLOSED("Closed - Offline");

    private final String type;
}
