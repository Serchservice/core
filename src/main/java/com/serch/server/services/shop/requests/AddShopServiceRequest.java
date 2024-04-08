package com.serch.server.services.shop.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class AddShopServiceRequest {
    private String service;
    private String shop;
}
