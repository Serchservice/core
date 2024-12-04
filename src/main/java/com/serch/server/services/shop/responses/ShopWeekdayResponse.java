package com.serch.server.services.shop.responses;

import lombok.Data;

@Data
public class ShopWeekdayResponse {
    private Long id;
    private String day;
    private String opening;
    private String closing;
    private Boolean open;
}