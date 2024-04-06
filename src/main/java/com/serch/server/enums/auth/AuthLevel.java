package com.serch.server.enums.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthLevel {
    LEVEL_1("Level 1"),
    LEVEL_2("Level 2");

    private final String type;
}
