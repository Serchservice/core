package com.serch.server.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserAction {
    LOGIN("Login"),
    SUPER_SIGNUP("Super Signup"),
    PASSWORD_RESET("Password Reset"),
    INVITE("Invite");

    private final String value;
}