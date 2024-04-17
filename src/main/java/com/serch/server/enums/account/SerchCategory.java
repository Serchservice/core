package com.serch.server.enums.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The SerchCategory enum represents different categories in the Serch application.
 * Each enum constant corresponds to a specific category and provides a descriptive type.
 * <p></p>
 * This enum is annotated with {@link lombok.Lombok}, {@link Getter} and {@link RequiredArgsConstructor} to generate
 * getter methods and a constructor with required arguments automatically.
 */
@Getter
@RequiredArgsConstructor
public enum SerchCategory {
    MECHANIC("Mechanic"),
    PLUMBER("Plumber"),
    ELECTRICIAN("Electrician"),
    BUSINESS("Business"),
    CARPENTER("Carpenter"),
    GUEST("Guest"),
    USER("User");

    private final String type;
}
