package com.serch.server.enums.verified;

import lombok.Getter;

/**
 * The ConsentType enum represents different types of consent in the application.
 * Each enum constant corresponds to a specific type of consent and provides a descriptive type.
 * <p></p>
 * The consent types are:
 * <ul>
 *     <li>{@link ConsentType#NONE} - Represents no consent provided</li>
 *     <li>{@link ConsentType#YES} - Represents consent given</li>
 *     <li>{@link ConsentType#NO} - Represents consent not given</li>
 * </ul>
 */
@Getter
public enum ConsentType {
    NONE("None"),
    YES("Yes"),
    NO("No");

    private final String type;
    ConsentType(String type) {
        this.type = type;
    }
}
