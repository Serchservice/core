package com.serch.server.services.shop.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class SearchShopResponse {
    private UUID user;
    private Double distance;

    @JsonProperty("distance_in_km")
    private String distanceInKm;
    private ShopResponse shop;
}