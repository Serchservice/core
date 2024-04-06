package com.serch.server.enums.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {
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
