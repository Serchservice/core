package com.serch.server.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PermissionScope {
    USER("User"),
    PROVIDER("Provider"),
    ASSOCIATE_PROVIDER("Associate Provider"),
    BUSINESS("Business"),
    PAYMENT("Payment"),
    SUPPORT("Support"),
    PRODUCT("Product"),
    ADMIN("Admin"),
    GUEST("Guest");

    private final String scope;
}