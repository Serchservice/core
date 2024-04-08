package com.serch.server.services.shop.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.account.SerchCategory;
import lombok.Data;

@Data
public class UpdateShopRequest {
    private String name;
    private SerchCategory category;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("shop_id")
    private String shopId;

    private String place;
    private String address;
    private Double latitude;
    private Double longitude;
}
