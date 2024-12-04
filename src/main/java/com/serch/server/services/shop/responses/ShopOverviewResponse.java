package com.serch.server.services.shop.responses;

import com.serch.server.enums.shop.ShopStatus;
import lombok.Data;

@Data
public class ShopOverviewResponse {
    private String name;
    private String category;
    private String image;
    private String logo;
    private Boolean open = false;
    private ShopStatus status;
    private Double rating = 0.0;
    private String id;
    private String address;
    private String phone;
    private Double latitude;
    private Double longitude;
}