package com.serch.server.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Permission {
    WRITE("permission::write"),
    READ("permission::read"),
    UPDATE("permission::update"),
    DELETE("permission::delete");

    private final String permission;
}