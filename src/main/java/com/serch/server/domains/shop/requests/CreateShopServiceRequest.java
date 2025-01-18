package com.serch.server.domains.shop.requests;

import lombok.Data;

@Data
public class CreateShopServiceRequest {
    private String id;
    private String service;
}