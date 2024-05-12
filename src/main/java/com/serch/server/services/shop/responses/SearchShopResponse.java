package com.serch.server.services.shop.responses;

import lombok.Data;

import java.util.UUID;

@Data
public class SearchShopResponse {
    private String distance;
    private UUID id;

    private ShopResponse shop;
}