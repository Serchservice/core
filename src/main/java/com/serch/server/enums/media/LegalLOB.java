package com.serch.server.enums.media;

import lombok.Getter;

/**
 * The LegalLOB enum represents different Lines of Business (LOB) in the legal context of the application.
 * Each enum constant corresponds to a specific Line of Business and provides a descriptive type.
 * <p></p>
 * The Lines of Business are:
 * <ul>
 *     <li>{@link LegalLOB#USER} - Represents the user line of business for requests</li>
 *     <li>{@link LegalLOB#GUEST} - Represents the guest line of business for requests</li>
 *     <li>{@link LegalLOB#PROVIDER} - Represents the independent provider line of business for providing services</li>
 *     <li>{@link LegalLOB#ASSOCIATE} - Represents the associate provider line of business</li>
 *     <li>{@link LegalLOB#BUSINESS} - Represents the business line of business for Serch businesses</li>
 * </ul>
 * @see Getter
 */
@Getter
public enum LegalLOB {
    GENERAL("General"),
    USER("Request/User"),
    GUEST("Request/Guest"),
    PROVIDER("Provide/Independent Provider"),
    ASSOCIATE("Associate Provider"),
    BUSINESS("Serch Business");

    private final String type;

    LegalLOB(String type) {
        this.type = type;
    }
}