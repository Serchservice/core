package com.serch.server.services.shop.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.shop.ShopStatus;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SearchShopResponse {
    private String distance;
    private String name;
    private SerchCategory category;
    private ShopStatus status;
    private Double rating;

    @JsonProperty("shop_id")
    private UUID serchId;

    @JsonProperty("place_id")
    private String placeId;
    private String address;
    private Double latitude;
    private Double longitude;

    private List<ShopServiceResponse> services;
}
