package com.serch.server.services.shop.requests;

import lombok.Data;

@Data
public class CreateShopServiceRequest {
    private String id;
    private String service;
}