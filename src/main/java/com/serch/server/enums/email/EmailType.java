package com.serch.server.enums.email;

/**
 * The EmailType enum represents different types of email actions in the application.
 * Each enum constant corresponds to a specific email action.
 * <p></p>
 * The email types are:
 * <ul>
 *     <li>{@link EmailType#SIGNUP} - Represents an email for signup</li>
 *     <li>{@link EmailType#LOGIN} - Represents an email for login</li>
 *     <li>{@link EmailType#RESET} - Represents an email for password reset</li>
 *     <li>{@link EmailType#UNSUCCESSFUL_PAYMENT} - Represents an email for unsuccessful payment</li>
 * </ul>
 */
public enum EmailType {
    SIGNUP,
    RESET,
    LOGIN,
    UNSUCCESSFUL_PAYMENT
}
