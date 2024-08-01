package com.serch.server.enums.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The AuthMethod enum represents different authentication methods in the application.
 * Each enum constant corresponds to a specific authentication method and provides a descriptive type.
 * <p></p>
 * The authentication methods are:
 * <ul>
 *     <li>{@link AuthMethod#TOKEN} - Authentication using a token</li>
 *     <li>{@link AuthMethod#PASSWORD} - Authentication using a password</li>
 *     <li>{@link AuthMethod#PASSWORD_CHANGE} - Authentication for changing password</li>
 *     <li>{@link AuthMethod#MFA} - Multi-Factor Authentication</li>
 *     <li>{@link AuthMethod#NONE} - No Authentication</li>
 * </ul>
 * This enum is annotated with Lombok's {@link Getter} and {@link RequiredArgsConstructor} to generate
 * getter methods and a constructor with required arguments automatically.
 */
@Getter
@RequiredArgsConstructor
public enum AuthMethod {
    TOKEN("Token"),
    PASSWORD("Password"),
    NONE("None"),
    PASSWORD_CHANGE("Password Change"),
    MFA("Multi-Factor Authentication");

    private final String type;
}

