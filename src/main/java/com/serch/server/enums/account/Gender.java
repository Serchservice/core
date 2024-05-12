package com.serch.server.enums.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {
    MALE("Male"),
    FEMALE("Female"),
    ANY("Any"),
    NONE("All");

    private final String type;
}
