package com.serch.server.enums.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthMethod {
    TOKEN("Token"),
    PASSWORD("Password"),
    PASSWORD_CHANGE("Password Change"),
    MFA("Multi-Factor Authentication");

    private final String type;
}
