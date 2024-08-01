package com.serch.server.services.shop.responses;

import com.serch.server.enums.shop.ShopStatus;
import lombok.Data;

import java.util.List;

@Data
public class ShopResponse {
    private String name;
    private String category;
    private String image;
    private String logo;
    private Boolean open;
    private ShopStatus status;
    private Double rating;
    private String id;
    private String address;
    private String phone;
    private Double latitude;
    private Double longitude;

    private ShopWeekdayResponse current;
    private List<ShopWeekdayResponse> weekdays;
    private List<ShopServiceResponse> services;
}