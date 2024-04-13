package com.serch.server.enums.company;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductType {
    ACCOUNT("Account"),
    CALL("Call"),
    CORE("Core"),
    GENERAL("General");

    private final String type;
}
