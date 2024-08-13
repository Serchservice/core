package com.serch.server.enums.email;

/**
 * The EmailType enum represents different types of email actions in the application.
 * Each enum constant corresponds to a specific email action.
 * <p></p>
 * The email types are:
 * <ul>
 *     <li>{@link EmailType#SIGNUP} - Represents an email for signup</li>
 *     <li>{@link EmailType#ADMIN_LOGIN} - Represents an email for admin login</li>
 *     <li>{@link EmailType#RESET_PASSWORD} - Represents an email for password reset</li>
 *     <li>{@link EmailType#UNSUCCESSFUL_PAYMENT} - Represents an email for unsuccessful payment</li>
 * </ul>
 */
public enum EmailType {
    SIGNUP,
    ADMIN_SIGNUP,
    RESET_PASSWORD,
    ASSOCIATE_INVITE,
    ADMIN_LOGIN,
    ADMIN_INVITE,
    ADMIN_PERMISSION,
    ADMIN_RESET_PASSWORD, UNSUCCESSFUL_PAYMENT
}
