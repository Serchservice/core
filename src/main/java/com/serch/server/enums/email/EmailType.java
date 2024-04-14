package com.serch.server.enums.email;

/**
 * The EmailType enum represents different types of email actions in the application.
 * Each enum constant corresponds to a specific email action.
 * <p></p>
 * The email types are:
 * <ul>
 *     <li>{@link EmailType#SIGNUP} - Represents an email for signup</li>
 *     <li>{@link EmailType#RESET} - Represents an email for password reset</li>
 * </ul>
 */
public enum EmailType {
    SIGNUP,
    RESET
}
