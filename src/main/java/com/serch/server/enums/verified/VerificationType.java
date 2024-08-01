package com.serch.server.enums.verified;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VerificationType {
    CONSENT("Verification Consent", 0);

    private final String name;
    private final Integer hierarchy;
}