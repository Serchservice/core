package com.serch.server.enums.company;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The ProductType enum represents different types of products in the application.
 * Each enum constant corresponds to a specific product type and provides a descriptive type.
 * <p></p>
 * The product types are:
 * <ul>
 *     <li>{@link ProductType#ACCOUNT} - Represents an account-related product</li>
 *     <li>{@link ProductType#CALL} - Represents a product related to calls</li>
 *     <li>{@link ProductType#CORE} - Represents a services product</li>
 *     <li>{@link ProductType#GENERAL} - Represents a general product</li>
 * </ul>
 * This enum is annotated with Lombok's {@link Getter} and {@link RequiredArgsConstructor}
 * to generate getter methods and a constructor with required arguments automatically.
 */
@Getter
@RequiredArgsConstructor
public enum ProductType {
    ACCOUNT("Account"),
    CALL("Call"),
    CORE("Core"),
    GENERAL("General");

    private final String type;
}
