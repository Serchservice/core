package com.serch.server.enums.shop;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShopStatus {
    OPEN("Open - Online"),
    SUSPENDED("Suspended - Offline"),
    CLOSED("Closed - Offline");

    private final String type;
}
