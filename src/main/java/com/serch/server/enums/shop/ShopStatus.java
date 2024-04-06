package com.serch.server.enums.shop;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShopStatus {
    ONLINE("Online"),
    OFFLINE("Offline");

    private final String type;
}
