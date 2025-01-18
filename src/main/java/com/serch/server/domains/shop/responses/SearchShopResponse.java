package com.serch.server.domains.shop.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class SearchShopResponse {
    private UUID user;
    private Double distance;

    @JsonProperty("is_google")
    private boolean isGoogle = false;

    @JsonProperty("distance_in_km")
    private String distanceInKm;
    private ShopResponse shop;
}