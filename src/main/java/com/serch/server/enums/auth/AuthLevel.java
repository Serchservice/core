package com.serch.server.enums.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The AuthLevel enum represents different authentication levels in the application.
 * Each enum constant corresponds to a specific authentication level and provides a descriptive type.
 * Such like:
 * <ul>
 *     <li>{@link AuthLevel#LEVEL_1} - The first level of authentication with JWT Token or Password</li>
 *     <li>{@link AuthLevel#LEVEL_2} - The second level of authentication with MFA {@link com.serch.server.models.auth.mfa.MFAFactor}</li>
 * </ul>
 * <p></p>
 * This enum is annotated with Lombok's {@link Getter} and {@link RequiredArgsConstructor} to generate
 * getter methods and a constructor with required arguments automatically.
 */
@Getter
@RequiredArgsConstructor
public enum AuthLevel {
    LEVEL_1("Level 1"),
    LEVEL_2("Level 2");

    private final String type;
}