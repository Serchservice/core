package com.serch.server.enums.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The Permission enum represents different permissions in the application.
 * Each enum constant corresponds to a specific permission and provides a descriptive type.
 * <p></p>
 * The permissions are categorized based on their target entities:
 * <ul>
 *     <li>{@link AuthPermission#USER_READ} - Permission to read user data</li>
 *     <li>{@link AuthPermission#USER_WRITE} - Permission to write user data</li>
 *     <li>{@link AuthPermission#USER_DELETE} - Permission to delete user data</li>
 *     <li>{@link AuthPermission#USER_UPDATE} - Permission to update user data</li>
 *     <li>{@link AuthPermission#PROVIDER_READ} - Permission to read provider data</li>
 *     <li>{@link AuthPermission#PROVIDER_WRITE} - Permission to write provider data</li>
 *     <li>{@link AuthPermission#PROVIDER_DELETE} - Permission to delete provider data</li>
 *     <li>{@link AuthPermission#PROVIDER_UPDATE} - Permission to update provider data</li>
 *     <li>{@link AuthPermission#BUSINESS_READ} - Permission to read business data</li>
 *     <li>{@link AuthPermission#BUSINESS_WRITE} - Permission to write business data</li>
 *     <li>{@link AuthPermission#BUSINESS_DELETE} - Permission to delete business data</li>
 *     <li>{@link AuthPermission#BUSINESS_UPDATE} - Permission to update business data</li>
 *     <li>{@link AuthPermission#ASSOCIATE_PROVIDER_READ} - Permission to read associate provider data</li>
 *     <li>{@link AuthPermission#ASSOCIATE_PROVIDER_WRITE} - Permission to write associate provider data</li>
 *     <li>{@link AuthPermission#ASSOCIATE_PROVIDER_DELETE} - Permission to delete associate provider data</li>
 *     <li>{@link AuthPermission#ASSOCIATE_PROVIDER_UPDATE} - Permission to update associate provider data</li>
 * </ul>
 * This enum is annotated with Lombok's {@link Getter} and {@link RequiredArgsConstructor} to generate
 * getter methods and a constructor with required arguments automatically.
 */
@Getter
@RequiredArgsConstructor
public enum AuthPermission {
    USER_READ("user::read"),
    USER_WRITE("user::write"),
    USER_DELETE("user::delete"),
    USER_UPDATE("user::update"),

    PROVIDER_READ("provider::read"),
    PROVIDER_WRITE("provider::write"),
    PROVIDER_DELETE("provider::delete"),
    PROVIDER_UPDATE("provider::update"),

    BUSINESS_READ("business::read"),
    BUSINESS_WRITE("business::write"),
    BUSINESS_DELETE("business::delete"),
    BUSINESS_UPDATE("business::update"),

    ASSOCIATE_PROVIDER_READ("associate::provider::read"),
    ASSOCIATE_PROVIDER_WRITE("associate::provider::write"),
    ASSOCIATE_PROVIDER_DELETE("associate::provider::delete"),
    ASSOCIATE_PROVIDER_UPDATE("associate::provider::update");

    private final String type;
}
