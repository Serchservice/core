package com.serch.server.services.trip.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ShoppingItemRequest {
    private String item;
    private Integer amount;
    private Integer quantity;
}
