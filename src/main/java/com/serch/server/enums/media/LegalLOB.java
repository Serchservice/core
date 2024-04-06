package com.serch.server.enums.media;

import lombok.Getter;

@Getter
public enum LegalLOB {
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