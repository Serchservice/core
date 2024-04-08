package com.serch.server.enums.company;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductType {
    ACCOUNT("Account"),
    TRIP("Trip"),
    TIP2FIX("Tip2Fix"),
    CORE("Core"),
    GENERAL("General");

    private final String type;
}
