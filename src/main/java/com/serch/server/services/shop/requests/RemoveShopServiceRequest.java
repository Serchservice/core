package com.serch.server.services.shop.requests;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RemoveShopServiceRequest {
    private Long id;
    private String shop;
}
