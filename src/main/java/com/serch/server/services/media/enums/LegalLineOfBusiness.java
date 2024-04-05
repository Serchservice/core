package com.serch.server.services.media.enums;

import lombok.Getter;

@Getter
public enum LegalLineOfBusiness {
    USER("Request/User"),
    GUEST("Request/Guest"),
    PROVIDER("Provide/Independent Provider"),
    ASSOCIATE("Associate Provider"),
    BUSINESS("Serch Business");

    private final String type;

    LegalLineOfBusiness(String type) {
        this.type = type;
    }
}