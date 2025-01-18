package com.serch.server.domains.shop.requests;

import lombok.Data;

@Data
public class UpdateShopServiceRequest {
    private Long id;
    private String shop;
    private String service;
}