package com.serch.server.services.shop.responses;

import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.shop.ShopStatus;
import lombok.Data;

import java.util.List;

@Data
public class ShopResponse {
    private String name;
    private SerchCategory category;
    private ShopStatus status;
    private Double rating;
    private String id;
    private String place;
    private String address;
    private Double latitude;
    private Double longitude;

    private List<ShopServiceResponse> services;
}
