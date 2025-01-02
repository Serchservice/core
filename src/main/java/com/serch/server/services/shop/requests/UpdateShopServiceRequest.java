package com.serch.server.services.shop.requests;

import lombok.Data;

@Data
public class UpdateShopServiceRequest {
    private Long id;
    private String shop;
    private String service;
}