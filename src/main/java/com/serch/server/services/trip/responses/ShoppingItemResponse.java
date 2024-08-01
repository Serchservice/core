package com.serch.server.services.trip.responses;

import lombok.Data;

@Data
public class ShoppingItemResponse {
    private Long id;
    private String item;
    private Integer quantity;
    private String amount;
    private String slip;
}