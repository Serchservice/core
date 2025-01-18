package com.serch.server.domains.shop.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ShopDriveRequest {
    @JsonProperty("shop_id")
    private String shopId;

    private String address;
    private Double latitude;
    private Double longitude;

    @JsonProperty("place_id")
    private String placeId;
}
