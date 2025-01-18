package com.serch.server.domains.trip.requests;

import lombok.Data;

@Data
public class ShoppingItemRequest {
    private String item;
    private Integer amount;
    private Integer quantity;
}
